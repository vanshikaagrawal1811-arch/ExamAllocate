package com.examallocate.service;

import com.examallocate.dto.*;
import com.examallocate.entity.*;
import com.examallocate.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * City pass -> room pass -> seat fill.
 *  - Each student is allocated in ITS OWN transaction, so a crash mid-run loses at most one student and the run can resume.
 *  - Only registrations without a seat are processed, so retries / double clicks never create duplicates (design doc 7.3/7.5).
 *  - Seats are claimed with an atomic "UPDATE ... WHERE occupied < capacity" counter, so rooms are never over capacity.
 *  - Every decision is appended to allocation_log (never overwritten) for explainability.
 */
@Service
public class AllocationService {
    private final RegistrationRepository registrationRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final ExamCenterRepository examCenterRepository;
    private final RoomRepository roomRepository;
    private final SpecialNeedsRequestRepository specialNeedsRepository;
    private final AllocationLogRepository logRepository;
    private final AdmitCardService admitCardService;
    private final RegistrationService registrationService;
    private final TransactionTemplate tx;
    private final Set<Long> running = ConcurrentHashMap.newKeySet(); // blocks a 2nd concurrent run on this instance

    public AllocationService(RegistrationRepository registrationRepository, SeatAllocationRepository seatAllocationRepository,
                             ExamCenterRepository examCenterRepository, RoomRepository roomRepository,
                             SpecialNeedsRequestRepository specialNeedsRepository, AllocationLogRepository logRepository,
                             AdmitCardService admitCardService, RegistrationService registrationService,
                             PlatformTransactionManager txManager) {
        this.registrationRepository = registrationRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.examCenterRepository = examCenterRepository;
        this.roomRepository = roomRepository;
        this.specialNeedsRepository = specialNeedsRepository;
        this.logRepository = logRepository;
        this.admitCardService = admitCardService;
        this.registrationService = registrationService;
        this.tx = new TransactionTemplate(txManager);
    }

    public AllocationRunResult run(Long sessionId) {
        if (!running.add(sessionId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An allocation run is already in progress for this session");
        try {
            List<Registration> pending = new ArrayList<>(registrationRepository.findUnallocated(sessionId));
            if (pending.isEmpty()) return new AllocationRunResult(sessionId, 0, 0, 0, 0, "NOTHING_TO_DO");

            Map<Long, String> accommodation = new HashMap<>(); // regId -> APPROVED accommodation
            for (Registration r : pending)
                specialNeedsRepository.findFirstByStudent_StudentIdAndApprovedStatus(r.getStudent().getStudentId(), "APPROVED")
                        .ifPresent(sn -> accommodation.put(r.getRegId(), sn.getAccommodationType()));

            // Constrained-first ordering: accommodations, then the most oversubscribed first-choice cities, then first come first served.
            Map<String, Long> demand = new HashMap<>();
            for (Registration r : pending) demand.merge(nz(r.getStudent().getPreferredCity1()), 1L, Long::sum);
            Map<String, Double> pressure = new HashMap<>();
            demand.forEach((city, d) -> pressure.put(city, d / (double) Math.max(1, roomRepository.totalCapacityInCity(city))));
            pending.sort(Comparator.comparing((Registration r) -> !accommodation.containsKey(r.getRegId()))
                    .thenComparingDouble(r -> -pressure.getOrDefault(nz(r.getStudent().getPreferredCity1()), 0.0))
                    .thenComparing(Registration::getRegId));

            int seated = 0, fallbacks = 0, unplaced = 0;
            for (Registration reg : pending) {
                try {
                    Integer res = tx.execute(status -> allocateOne(reg, accommodation.get(reg.getRegId())));
                    if (res == null || res < 0) unplaced++;
                    else { seated++; if (res > 0) fallbacks++; }
                } catch (Exception e) {
                    unplaced++; // that student's transaction rolled back; the rest of the run continues
                }
            }
            return new AllocationRunResult(sessionId, pending.size(), seated, fallbacks, unplaced,
                    unplaced == 0 ? "COMPLETED" : "COMPLETED_WITH_UNPLACED");
        } finally {
            running.remove(sessionId);
        }
    }

    /** @return -1 unplaced, 0 got first choice, 1 got a fallback */
    private int allocateOne(Registration reg, String accommodation) {
        Student student = reg.getStudent();
        Long sessionId = reg.getSession().getSessionId();
        String school = student.getSchoolName();
        boolean ground = accommodation != null && (accommodation.contains("Wheelchair") || accommodation.contains("Accessible"));
        Registration regRef = registrationRepository.getReferenceById(reg.getRegId());

        LinkedHashSet<String> cities = new LinkedHashSet<>();
        if (!nz(student.getPreferredCity1()).isBlank()) cities.add(student.getPreferredCity1());
        if (!nz(student.getPreferredCity2()).isBlank()) cities.add(student.getPreferredCity2());
        cities.addAll(examCenterRepository.findDistinctCities()); // last resort: nearest alternate

        int idx = 0;
        for (String city : cities) {
            for (Room room : roomRepository.findByCenter_CityOrderByRoomId(city)) {
                if (ground && (room.getFloor() == null || room.getFloor() > 1)) continue; // accommodation: lowest floor only
                if (school != null && !school.isBlank()
                        && seatAllocationRepository.countSameSchool(room.getRoomId(), sessionId, school) > 0) continue; // anti-cheating
                roomRepository.ensureOccupancyRow(sessionId, room.getRoomId());
                if (roomRepository.tryReserveSeat(sessionId, room.getRoomId(), room.getCapacity()) == 0) continue; // room full
                int seatNo = roomRepository.currentOccupied(sessionId, room.getRoomId());

                SeatAllocation seat = new SeatAllocation();
                seat.setRegistration(regRef);
                seat.setCenter(room.getCenter());
                seat.setRoom(room);
                seat.setSeatNumber(seatNo);
                seatAllocationRepository.save(seat);

                String reason = idx == 0 ? "Allotted preferred city " + city
                        : (idx == 1 && city.equals(student.getPreferredCity2()))
                            ? "FALLBACK: first-choice city full or constraints unmet - assigned second choice (" + city + ")"
                            : "FALLBACK: both preferred cities unavailable - assigned nearest alternate city " + city;
                if (ground) reason += "; accommodation '" + accommodation + "' honored (lowest floor)";
                if (school != null && !school.isBlank()) reason += "; same-school check passed";
                writeLog(regRef, room.getCenter(), reason);
                admitCardService.generate(reg.getRegId(), sessionId);
                return idx == 0 ? 0 : 1;
            }
            idx++;
        }
        writeLog(regRef, null, ground
                ? "UNPLACED: accommodation '" + accommodation + "' could not be honored in any city - sent for admin review"
                : "UNPLACED: no room satisfied capacity and same-school rules - sent for admin review");
        return -1;
    }

    private void writeLog(Registration regRef, ExamCenter center, String reason) {
        AllocationLog l = new AllocationLog();
        l.setRegistration(regRef);
        l.setCenter(center);
        l.setReason(reason);
        l.setLoggedAt(LocalDateTime.now());
        logRepository.save(l);
    }

    private static String nz(String s) { return s == null ? "" : s; }

    // ---------------- read side ----------------
    @Transactional(readOnly = true)
    public AllocationStatusResponse status(Long regId, String applicationNo) {
        Registration reg = registrationService.requireOwned(regId, applicationNo);
        String exam = reg.getSession().getExamName();
        List<AllocationLog> logs = logRepository.findByRegistration_RegIdOrderByLogIdDesc(regId);
        String reason = logs.isEmpty() ? null : logs.get(0).getReason();
        LocalDateTime when = logs.isEmpty() ? null : logs.get(0).getLoggedAt();
        return seatAllocationRepository.findByRegistration_RegId(regId)
                .map(s -> new AllocationStatusResponse(regId, true, exam, s.getCenter().getCenterName(), s.getCenter().getCity(),
                        s.getCenter().getAddress(), s.getRoom().getRoomNumber(), s.getRoom().getFloor(), s.getSeatNumber(), reason, when))
                .orElse(new AllocationStatusResponse(regId, false, exam, null, null, null, null, null, null,
                        reason != null ? reason : "Allocation has not run for your session yet", when));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> explanation(Long regId, String applicationNo) {
        registrationService.requireOwned(regId, applicationNo);
        return logRepository.findByRegistration_RegIdOrderByLogIdDesc(regId).stream()
                .map(l -> Map.<String, Object>of("reason", String.valueOf(l.getReason()), "loggedAt", String.valueOf(l.getLoggedAt()))).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> analytics(Long sessionId) {
        long regs = registrationRepository.countBySession_SessionId(sessionId);
        long seated = seatAllocationRepository.countBySession(sessionId);
        long fb = logRepository.countFallbacks(sessionId);
        double pct = seated == 0 ? 0 : Math.round(1000.0 * fb / seated) / 10.0;
        return Map.of("registrations", regs, "seated", seated, "unseated", regs - seated, "fallbacks", fb, "fallbackRatePct", pct);
    }
}
