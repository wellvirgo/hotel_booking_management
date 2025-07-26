package vn.dangthehao.hotel_booking_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  int countByHotelId(Long hotelId);
}
