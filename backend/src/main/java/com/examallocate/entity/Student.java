package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "student")
@Getter @Setter @NoArgsConstructor
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    @Column(unique = true, nullable = false) private String applicationNo;
    @Column(nullable = false) private String name;
    private LocalDate dob;
    private String gender, category, phone, email, address, city, state, pincode;
    @Column(name = "preferred_city_1") private String preferredCity1;
    @Column(name = "preferred_city_2") private String preferredCity2;
    private String schoolName;
}
