package com.examallocate.controller;

import com.examallocate.dto.AdmitCardResponse;
import com.examallocate.service.AdmitCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admit-cards")
@RequiredArgsConstructor
public class AdmitCardController {
    private final AdmitCardService admitCardService;

    @GetMapping("/{regId}")
    public AdmitCardResponse get(@PathVariable Long regId, @RequestParam String applicationNo) {
        return admitCardService.get(regId, applicationNo);
    }
}
