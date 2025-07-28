package vn.dangthehao.hotel_booking_management.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.RefreshToken;
import vn.dangthehao.hotel_booking_management.model.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  Optional<RefreshToken> findByUser(User user);
}
