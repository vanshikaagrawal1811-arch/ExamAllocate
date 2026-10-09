package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SpecialNeedsRequestRepository extends JpaRepository<SpecialNeedsRequest, Long> {
    Optional<SpecialNeedsRequest> findFirstByStudent_StudentIdAndApprovedStatus(Long studentId, String status);
    List<SpecialNeedsRequest> findByApprovedStatus(String status);
}
