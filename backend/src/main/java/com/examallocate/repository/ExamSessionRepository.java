package com.examallocate.repository;

import com.examallocate.entity.ExamSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    Optional<ExamSession> findFirstByExamNameOrderByExamDateAscStartTimeAsc(String examName);
    List<ExamSession> findAllByOrderByExamDateAscStartTimeAsc();

    @Query("select distinct s.examName from ExamSession s order by s.examName")
    List<String> findCourseNames();
}
