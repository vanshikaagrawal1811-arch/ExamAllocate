package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {
    Optional<SeatAllocation> findByRegistration_RegId(Long regId);

    /** Anti-cheating: students from the same school already seated in this room for this session. */
    @Query("select count(sa) from SeatAllocation sa where sa.room.roomId = :roomId " +
           "and sa.registration.session.sessionId = :sid and lower(sa.registration.student.schoolName) = lower(:school)")
    long countSameSchool(@Param("roomId") Long roomId, @Param("sid") Long sid, @Param("school") String school);

    @Query("select count(sa) from SeatAllocation sa where sa.registration.session.sessionId = :sid")
    long countBySession(@Param("sid") Long sid);
}
