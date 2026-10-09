package com.examallocate.controller;

import com.examallocate.config.AdminGuard;
import com.examallocate.dto.*;
import com.examallocate.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AllocationController {
    private final AllocationService allocationService;
    private final AdminGuard guard;

    /** Admin only. Safe to call twice: already-seated students are skipped. */
    @PostMapping("/allocations/run/{sessionId}")
    public AllocationRunResult run(@RequestHeader(value = "X-Admin-Key", required = false) String key, @PathVariable Long sessionId) {
        guard.check(key);
        return allocationService.run(sessionId);
    }

    @GetMapping("/allocations/{regId}")
    public AllocationStatusResponse status(@PathVariable Long regId, @RequestParam String applicationNo) {
        return allocationService.status(regId, applicationNo);
    }

    @GetMapping("/allocations/{regId}/explanation")
    public List<Map<String, Object>> explanation(@PathVariable Long regId, @RequestParam String applicationNo) {
        return allocationService.explanation(regId, applicationNo);
    }

    @GetMapping("/analytics/{sessionId}")
    public Map<String, Object> analytics(@RequestHeader(value = "X-Admin-Key", required = false) String key, @PathVariable Long sessionId) {
        guard.check(key);
        return allocationService.analytics(sessionId);
    }
}
