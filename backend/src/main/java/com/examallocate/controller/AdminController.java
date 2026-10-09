package com.examallocate.controller;

import com.examallocate.config.AdminGuard;
import com.examallocate.dto.AdminRequests.*;
import com.examallocate.entity.*;
import com.examallocate.repository.ExamCenterRepository;
import com.examallocate.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ExamCenterRepository centerRepository;
    private final AdminGuard guard;

    @GetMapping("/centers")
    public List<ExamCenter> centers() { return centerRepository.findAll(); }

    @PostMapping("/centers") @ResponseStatus(HttpStatus.CREATED)
    public ExamCenter addCenter(@RequestHeader(value = "X-Admin-Key", required = false) String key, @Valid @RequestBody CenterRequest r) {
        guard.check(key); return adminService.addCenter(r);
    }

    @PostMapping("/rooms") @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addRoom(@RequestHeader(value = "X-Admin-Key", required = false) String key, @Valid @RequestBody RoomRequest r) {
        guard.check(key); return adminService.addRoom(r);
    }

    @PostMapping("/sessions") @ResponseStatus(HttpStatus.CREATED)
    public ExamSession addSession(@RequestHeader(value = "X-Admin-Key", required = false) String key, @Valid @RequestBody SessionRequest r) {
        guard.check(key); return adminService.addSession(r);
    }

    @GetMapping("/special-needs-requests")
    public List<Map<String, Object>> pending(@RequestHeader(value = "X-Admin-Key", required = false) String key) {
        guard.check(key); return adminService.pendingSpecialNeeds();
    }

    @PatchMapping("/special-needs-requests/{id}")
    public Map<String, Object> decide(@RequestHeader(value = "X-Admin-Key", required = false) String key,
                                      @PathVariable Long id, @Valid @RequestBody StatusUpdate body) {
        guard.check(key); return adminService.decideSpecialNeeds(id, body.status());
    }
}
