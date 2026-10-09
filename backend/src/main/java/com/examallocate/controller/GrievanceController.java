package com.examallocate.controller;

import com.examallocate.config.AdminGuard;
import com.examallocate.dto.AdminRequests.StatusUpdate;
import com.examallocate.dto.GrievanceRequest;
import com.examallocate.service.GrievanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/grievances")
@RequiredArgsConstructor
public class GrievanceController {
    private final GrievanceService grievanceService;
    private final AdminGuard guard;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> file(@Valid @RequestBody GrievanceRequest req) { return grievanceService.file(req); }

    @GetMapping("/{id}")
    public Map<String, Object> track(@PathVariable Long id, @RequestParam String applicationNo) { return grievanceService.track(id, applicationNo); }

    @GetMapping
    public List<Map<String, Object>> all(@RequestHeader(value = "X-Admin-Key", required = false) String key) {
        guard.check(key);
        return grievanceService.listAll();
    }

    @PatchMapping("/{id}/resolve")
    public Map<String, Object> resolve(@RequestHeader(value = "X-Admin-Key", required = false) String key,
                                       @PathVariable Long id, @Valid @RequestBody StatusUpdate body) {
        guard.check(key);
        return grievanceService.resolve(id, body.status(), body.resolution());
    }
}
