package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findAllByOrderByGrievanceIdDesc();
}
