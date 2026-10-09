package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "exam_center")
@Getter @Setter @NoArgsConstructor
public class ExamCenter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long centerId;
    @Column(nullable = false) private String centerName;
    @Column(nullable = false) private String city;
    private String address;
    @Column(nullable = false) private Integer totalCapacity;
}
