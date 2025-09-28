package vn.dangthehao.hotel_booking_management.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.dangthehao.hotel_booking_management.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("select u from User u where u.id=:id")
  Optional<User> findByIdAndDeletedFalseFetchRole(Long id);

  Optional<User> findByUsernameAndDeletedFalse(String username);

  Optional<User> findByEmailAndDeletedFalse(String email);

  List<User> findAllByDeletedFalse();

  boolean existsByUsernameAndDeletedFalse(String username);

  boolean existsByEmailAndDeletedFalse(String email);

  @Query("select u.tokenVersion from User u where u.id=:id")
  Optional<Integer> findTokenVersionById(Long id);

  @EntityGraph(attributePaths = {})
  Optional<User> findByIdAndDeletedFalse(Long id);
}
