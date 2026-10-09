package com.examallocate.service;

import com.examallocate.dto.AdminRequests.*;
import com.examallocate.entity.*;
import com.examallocate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ExamCenterRepository centerRepository;
    private final RoomRepository roomRepository;
    private final ExamSessionRepository sessionRepository;
    private final SpecialNeedsRequestRepository specialNeedsRepository;

    public ExamCenter addCenter(CenterRequest r) {
        ExamCenter c = new ExamCenter();
        c.setCenterName(r.centerName()); c.setCity(r.city().trim()); c.setAddress(r.address()); c.setTotalCapacity(r.totalCapacity());
        return centerRepository.save(c);
    }

    @Transactional
    public Map<String, Object> addRoom(RoomRequest r) {
        ExamCenter c = centerRepository.findById(r.centerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown center"));
        Room room = new Room();
        room.setCenter(c); room.setRoomNumber(r.roomNumber()); room.setCapacity(r.capacity()); room.setFloor(r.floor());
        room = roomRepository.save(room);
        return Map.of("roomId", room.getRoomId(), "centerId", c.getCenterId(), "roomNumber", room.getRoomNumber());
    }

    public ExamSession addSession(SessionRequest r) {
        ExamSession s = new ExamSession();
        s.setExamName(r.examName()); s.setExamDate(r.examDate()); s.setShiftName(r.shiftName());
        s.setStartTime(r.startTime()); s.setEndTime(r.endTime());
        return sessionRepository.save(s);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> pendingSpecialNeeds() {
        return specialNeedsRepository.findByApprovedStatus("PENDING").stream().map(sn -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("requestId", sn.getRequestId());
            m.put("studentName", sn.getStudent().getName());
            m.put("applicationNo", sn.getStudent().getApplicationNo());
            m.put("accommodationType", sn.getAccommodationType());
            return m;
        }).toList();
    }

    @Transactional
    public Map<String, Object> decideSpecialNeeds(Long id, String status) {
        if (!List.of("APPROVED", "REJECTED").contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be APPROVED or REJECTED");
        SpecialNeedsRequest sn = specialNeedsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such request"));
        sn.setApprovedStatus(status);
        return Map.of("requestId", id, "status", status);
    }
}
