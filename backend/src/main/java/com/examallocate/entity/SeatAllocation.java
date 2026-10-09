package com.examallocate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "seat_allocation")
@Getter @Setter @NoArgsConstructor
public class SeatAllocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reg_id") private Registration registration;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "center_id") private ExamCenter center;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "room_id") private Room room;
    @Column(nullable = false) private Integer seatNumber;
    @Version private Long version; // optimistic locking (design doc 4.3)
}
