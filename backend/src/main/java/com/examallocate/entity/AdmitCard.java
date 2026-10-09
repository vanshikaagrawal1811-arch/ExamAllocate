package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "admit_card")
@Getter @Setter @NoArgsConstructor
public class AdmitCard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admitCardId;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reg_id") private Registration registration;
    @Column(unique = true, nullable = false) private String hallTicketNo;
    private LocalDate issueDate;
    private Integer downloadCount = 0;
}
