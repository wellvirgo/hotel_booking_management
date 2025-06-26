package vn.dangthehao.hotel_booking_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.model.Hotel;

import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Query("select new vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO(" +
            "h.hotelName, ow.fullName, h.address, h.depositRate, h.depositDeadlineHours) " +
            "from Hotel h join h.owner ow " +
            "where h.isApproved=false and h.isDeleted=false and h.status=:status")
    Page<UnapprovedHotelDTO> findUnapprovedHotels(Pageable pageable, HotelStatus status);

    Optional<Hotel> findByIdAndIsDeletedFalse(Long id);
}
