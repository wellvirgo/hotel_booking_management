package vn.dangthehao.hotel_booking_management.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;
import vn.dangthehao.hotel_booking_management.model.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

  @Query(
      "select new vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO("
          + "rt.name, rt.description, rt.pricePerNight, rt.isActive) "
          + "from RoomType rt "
          + "where rt.hotel.id=:hotelId")
  Page<OwnerRoomTypeDTO> findByHotelId(Long hotelId, Pageable pageable);

  List<RoomType> findByHotelIdInAndCapacityGreaterThanEqual(List<Long> hotelIds, int numGuests);

  Optional<RoomType> findByIdAndIsActiveTrue(Long id);
}
