package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "special_needs_request")
@Getter @Setter @NoArgsConstructor
public class SpecialNeedsRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "student_id") private Student student;
    private String accommodationType;
    private String approvedStatus; // PENDING / APPROVED / REJECTED
}
