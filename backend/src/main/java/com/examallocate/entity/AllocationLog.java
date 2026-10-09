package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "allocation_log")
@Getter @Setter @NoArgsConstructor
public class AllocationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reg_id") private Registration registration;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "center_id") private ExamCenter center; // null when unplaced
    @Column(length = 500) private String reason;
    private LocalDateTime loggedAt;
}
