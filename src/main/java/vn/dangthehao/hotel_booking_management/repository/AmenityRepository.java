package vn.dangthehao.hotel_booking_management.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.Amenity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Optional<Amenity> findByName(String name);
    Set<Amenity> findByNameIn(Set<String> names);
}
