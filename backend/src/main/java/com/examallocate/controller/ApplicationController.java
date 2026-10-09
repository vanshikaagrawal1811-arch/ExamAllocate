package com.examallocate.controller;

import com.examallocate.dto.*;
import com.examallocate.repository.ExamSessionRepository;
import com.examallocate.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {
    private final RegistrationService registrationService;
    private final ExamSessionRepository sessionRepository;

    @PostMapping("/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplyResponse apply(@Valid @RequestBody ApplyRequest request) {
        return registrationService.apply(request);
    }

    @GetMapping("/sessions")
    public List<com.examallocate.entity.ExamSession> sessions() {
        return sessionRepository.findAllByOrderByExamDateAscStartTimeAsc();
    }

    @GetMapping("/courses")
    public List<String> courses() {
        return sessionRepository.findCourseNames();
    }

    @GetMapping("/registrations/lookup")
    public RegistrationLookupResponse lookup(
            @RequestParam String applicationNo,
            @RequestParam String email) {
        return registrationService.lookup(applicationNo, email);
    }
}
