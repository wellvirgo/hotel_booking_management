package vn.dangthehao.hotel_booking_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
}
