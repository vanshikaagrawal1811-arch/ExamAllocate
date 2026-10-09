package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByStudent_StudentIdAndSession_SessionId(Long studentId, Long sessionId);
    long countBySession_SessionId(Long sessionId);

    Optional<Registration> findFirstByStudent_ApplicationNoAndStudent_EmailIgnoreCase(
            String applicationNo, String email);

    @Query("select r from Registration r join fetch r.student join fetch r.session " +
           "where r.session.sessionId = :sid and not exists (select 1 from SeatAllocation sa where sa.registration = r)")
    List<Registration> findUnallocated(@Param("sid") Long sid);
}
