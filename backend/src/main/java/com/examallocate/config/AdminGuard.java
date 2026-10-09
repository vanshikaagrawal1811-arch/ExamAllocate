package com.examallocate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Lightweight admin protection: admin endpoints need header X-Admin-Key. Replace with Spring Security + JWT for production. */
@Component
public class AdminGuard {
    @Value("${examallocate.admin-key}")
    private String adminKey;

    public void check(String providedKey) {
        if (providedKey == null || !providedKey.equals(adminKey))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
    }
}
