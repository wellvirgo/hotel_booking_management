package vn.dangthehao.hotel_booking_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelItemDTO;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.model.Hotel;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Query("select new vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO(" +
            "h.hotelName, ow.fullName, h.address, h.location, h.depositRate, h.depositDeadlineHours) " +
            "from Hotel h join h.owner ow " +
            "where h.isApproved=false and h.isDeleted=false and h.status=:status")
    Page<UnapprovedHotelDTO> findUnapprovedHotels(Pageable pageable, HotelStatus status);

    Optional<Hotel> findByIdAndIsDeletedFalse(Long id);

    @Query("select new vn.dangthehao.hotel_booking_management.dto.OwnerHotelItemDTO(" +
            "h.id, h.hotelName, h.address, h.createdAt, h.status, h.rating) " +
            "from Hotel h join h.owner ow " +
            "where ow.id=:id and h.isApproved=true and h.isDeleted=false ")
    Page<OwnerHotelItemDTO> findApprovedHotelsByOwner(Pageable pageable, Long id);

    @Query("select new vn.dangthehao.hotel_booking_management.dto.OwnerHotelItemDTO(" +
            "h.id, h.hotelName, h.address, h.createdAt, h.status, h.rating) " +
            "from Hotel h join h.owner ow " +
            "where ow.id=:id and h.isApproved=false and h.isDeleted=false ")
    Page<OwnerHotelItemDTO> findUnApprovedHotelsByOwner(Pageable pageable, Long id);

    @EntityGraph(attributePaths = {})
    Page<Hotel> findByLocationAndIsApprovedTrueAndIsDeletedFalse(String location, Pageable pageable);

    @EntityGraph(attributePaths = {})
    Page<Hotel> findByLocation(String location, Pageable pageable);
}
