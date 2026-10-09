package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "registration")
@Getter @Setter @NoArgsConstructor
public class Registration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "student_id") private Student student;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "session_id") private ExamSession session;
    private LocalDateTime regDate;
}
