package com.examallocate.repository;

import com.examallocate.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AdmitCardRepository extends JpaRepository<AdmitCard, Long> {
    Optional<AdmitCard> findByRegistration_RegId(Long regId);
}
