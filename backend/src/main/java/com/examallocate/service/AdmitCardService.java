package com.examallocate.service;

import com.examallocate.dto.AdmitCardResponse;
import com.examallocate.entity.*;
import com.examallocate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdmitCardService {
    private final AdmitCardRepository admitCardRepository;
    private final RegistrationRepository registrationRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final RegistrationService registrationService;

    /** Called by the engine inside the same transaction as the seat, so a seat never exists without its card. */
    @Transactional
    public void generate(Long regId, Long sessionId) {
        if (admitCardRepository.findByRegistration_RegId(regId).isPresent()) return;
        AdmitCard card = new AdmitCard();
        card.setRegistration(registrationRepository.getReferenceById(regId));
        card.setHallTicketNo(String.format("HT-%d-%06d", sessionId, regId));
        card.setIssueDate(LocalDate.now());
        card.setDownloadCount(0);
        admitCardRepository.save(card);
    }

    @Transactional
    public AdmitCardResponse get(Long regId, String applicationNo) {
        Registration reg = registrationService.requireOwned(regId, applicationNo);
        AdmitCard card = admitCardRepository.findByRegistration_RegId(regId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Admit card not issued yet - allocation has not run for you"));
        SeatAllocation seat = seatAllocationRepository.findByRegistration_RegId(regId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No seat allocated yet"));
        card.setDownloadCount((card.getDownloadCount() == null ? 0 : card.getDownloadCount()) + 1);
        ExamSession s = reg.getSession();
        return new AdmitCardResponse(card.getHallTicketNo(), card.getIssueDate(), reg.getStudent().getName(),
                reg.getStudent().getApplicationNo(), s.getExamName(), s.getExamDate(), s.getShiftName(), s.getStartTime(),
                s.getEndTime(), seat.getCenter().getCenterName(), seat.getCenter().getAddress(), seat.getCenter().getCity(),
                seat.getRoom().getRoomNumber(), seat.getRoom().getFloor(), seat.getSeatNumber());
    }
}
