package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "grievance")
@Getter @Setter @NoArgsConstructor
public class Grievance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grievanceId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reg_id") private Registration registration;
    @Column(length = 1000) private String description;
    private LocalDateTime filedDate;
    private String status; // OPEN -> UNDER_REVIEW -> RESOLVED | REJECTED
    @Column(length = 1000) private String resolution;
}
