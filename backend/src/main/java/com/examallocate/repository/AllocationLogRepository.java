package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AllocationLogRepository extends JpaRepository<AllocationLog, Long> {
    List<AllocationLog> findByRegistration_RegIdOrderByLogIdDesc(Long regId);

    @Query("select count(distinct l.registration.regId) from AllocationLog l " +
           "where l.registration.session.sessionId = :sid and l.reason like 'FALLBACK%'")
    long countFallbacks(@Param("sid") Long sid);
}
