package vn.dangthehao.hotel_booking_management.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.dangthehao.hotel_booking_management.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByIdAndDeletedFalse(Long id);

  Optional<User> findByUsernameAndDeletedFalse(String username);

  Optional<User> findByEmailAndDeletedFalse(String email);

  List<User> findAllByDeletedFalse();

  boolean existsByUsernameAndDeletedFalse(String username);

  boolean existsByEmailAndDeletedFalse(String email);
}
