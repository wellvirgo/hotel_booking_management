package vn.dangthehao.hotel_booking_management.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.dto.AdminHotelListItemDTO;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelListItemDTO;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
  @Query("select h from Hotel h where h.id=:id and h.deleted=false")
  Optional<Hotel> findByIdFetchOwner(Long id);

  @EntityGraph(attributePaths = {})
  Optional<Hotel> findByIdAndDeletedFalse(Long id);

  @Query(
      "select new vn.dangthehao.hotel_booking_management.dto.AdminHotelListItemDTO("
          + " h.id, h.hotelName, ow.fullName, h.address, h.location, h.thumbnail, h.rating)"
          + " from Hotel h join h.owner ow"
          + " where h.deleted = false and h.status=:status")
  Page<AdminHotelListItemDTO> findHotelsForAdminByStatus(Pageable pageable, HotelStatus status);

  @Query(
      "select new vn.dangthehao.hotel_booking_management.dto.OwnerHotelListItemDTO("
          + " h.id, h.hotelName, h.address, h.location, h.thumbnail, h.rating)"
          + " from Hotel h"
          + " where h.deleted = false and h.owner.id=:ownerId and h.status=:status")
  @EntityGraph(attributePaths = {})
  Page<OwnerHotelListItemDTO> findHotelsForOwnerByOwnerIdAndStatus(
      Pageable pageable, Long ownerId, HotelStatus status);

  @EntityGraph(attributePaths = {})
  Optional<Hotel> findByIdAndStatusAndDeletedFalse(Long id, HotelStatus status);

  @EntityGraph(attributePaths = {})
  Page<Hotel> findByLocationAndDeletedFalse(String location, Pageable pageable);
}
