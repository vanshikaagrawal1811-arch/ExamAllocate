package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "exam_session")
@Getter @Setter @NoArgsConstructor
public class ExamSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;
    @Column(nullable = false) private String examName;
    @Column(nullable = false) private LocalDate examDate;
    @Column(nullable = false) private String shiftName;
    private LocalTime startTime, endTime;
}
