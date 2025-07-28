package vn.dangthehao.hotel_booking_management.repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

  Optional<Amenity> findByName(String name);

  Set<Amenity> findByNameIn(Set<String> names);
}
