package vn.dangthehao.hotel_booking_management.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.dangthehao.hotel_booking_management.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByIdAndIsDeletedFalse(Long id);

  Optional<User> findByUsernameAndIsDeletedFalse(String username);

  Optional<User> findByEmailAndIsDeletedFalse(String email);

  List<User> findAllByIsDeletedFalse();

  boolean existsByUsernameAndIsDeletedFalse(String username);

  boolean existsByEmailAndIsDeletedFalse(String email);
}
