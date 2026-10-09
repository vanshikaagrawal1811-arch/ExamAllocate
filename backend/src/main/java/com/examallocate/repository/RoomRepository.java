package com.examallocate.repository;

import com.examallocate.entity.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCenter_CityOrderByRoomId(String city);

    @Query("select coalesce(sum(r.capacity), 0) from Room r where r.center.city = :city")
    long totalCapacityInCity(@Param("city") String city);

    // ---- atomic seat counter (design doc 7.2): UPDATE ... WHERE occupied < capacity, check affected rows ----
    @Modifying
    @Query(value = "INSERT IGNORE INTO room_occupancy(session_id, room_id, occupied) VALUES (:sid, :rid, 0)", nativeQuery = true)
    void ensureOccupancyRow(@Param("sid") Long sid, @Param("rid") Long rid);

    /** 1 = seat reserved, 0 = room full. Two concurrent runs can never both take the last seat. */
    @Modifying
    @Query(value = "UPDATE room_occupancy SET occupied = occupied + 1 " +
                   "WHERE session_id = :sid AND room_id = :rid AND occupied < :cap", nativeQuery = true)
    int tryReserveSeat(@Param("sid") Long sid, @Param("rid") Long rid, @Param("cap") int cap);

    @Query(value = "SELECT occupied FROM room_occupancy WHERE session_id = :sid AND room_id = :rid", nativeQuery = true)
    int currentOccupied(@Param("sid") Long sid, @Param("rid") Long rid);
}
