package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByApplicationNo(String applicationNo);
}
