package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
    @Query("select distinct c.city from ExamCenter c order by c.city")
    List<String> findDistinctCities();
}
