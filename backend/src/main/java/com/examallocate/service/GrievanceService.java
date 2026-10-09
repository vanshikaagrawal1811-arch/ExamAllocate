package com.examallocate.service;

import com.examallocate.dto.GrievanceRequest;
import com.examallocate.entity.*;
import com.examallocate.repository.GrievanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GrievanceService {
    private final GrievanceRepository grievanceRepository;
    private final RegistrationService registrationService;

    // State machine (design doc 10). RESOLVED/REJECTED are terminal: a recurring problem is filed as a NEW grievance.
    private static final Map<String, Set<String>> NEXT = Map.of(
            "OPEN", Set.of("UNDER_REVIEW"),
            "UNDER_REVIEW", Set.of("RESOLVED", "REJECTED"));

    @Transactional
    public Map<String, Object> file(GrievanceRequest req) {
        Registration reg = registrationService.requireOwned(req.regId(), req.applicationNo());
        Grievance g = new Grievance();
        g.setRegistration(reg);
        g.setDescription(req.description().trim());
        g.setFiledDate(LocalDateTime.now());
        g.setStatus("OPEN");
        return view(grievanceRepository.save(g));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> track(Long id, String applicationNo) {
        Grievance g = grievanceRepository.findById(id).orElse(null);
        if (g == null || applicationNo == null || !g.getRegistration().getStudent().getApplicationNo().equals(applicationNo.trim()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No grievance matches that ID and application number");
        return view(g);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listAll() {
        return grievanceRepository.findAllByOrderByGrievanceIdDesc().stream().map(this::view).toList();
    }

    @Transactional
    public Map<String, Object> resolve(Long id, String newStatus, String resolution) {
        Grievance g = grievanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such grievance"));
        if (!NEXT.getOrDefault(g.getStatus(), Set.of()).contains(newStatus))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Illegal transition " + g.getStatus() + " -> " + newStatus);
        g.setStatus(newStatus);
        if (resolution != null && !resolution.isBlank()) g.setResolution(resolution.trim());
        return view(g);
    }

    private Map<String, Object> view(Grievance g) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("grievanceId", g.getGrievanceId());
        m.put("regId", g.getRegistration().getRegId());
        m.put("studentName", g.getRegistration().getStudent().getName());
        m.put("description", g.getDescription());
        m.put("filedDate", g.getFiledDate());
        m.put("status", g.getStatus());
        m.put("resolution", g.getResolution());
        return m;
    }
}
