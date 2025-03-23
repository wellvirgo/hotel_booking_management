package vn.dangthehao.hotel_booking_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.model.InvalidToken;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM InvalidToken it where it.expiredTime < CURRENT_TIMESTAMP")
    void deleteByExpiredTime();
}
