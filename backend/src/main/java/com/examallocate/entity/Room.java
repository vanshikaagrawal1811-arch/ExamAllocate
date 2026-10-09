package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "room")
@Getter @Setter @NoArgsConstructor
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "center_id") private ExamCenter center;
    @Column(nullable = false) private String roomNumber;
    @Column(nullable = false) private Integer capacity;
    private Integer floor;
}
