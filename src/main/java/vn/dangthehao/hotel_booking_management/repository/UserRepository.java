package vn.dangthehao.hotel_booking_management.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.dangthehao.hotel_booking_management.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("select u from User u where u.id=:id")
  Optional<User> findByIdAndDeletedFalseFetchRole(Long id);

  @Query("select u from User u where u.username=:username and u.deleted = false")
  Optional<User> findByUsernameAndDeletedFalseFetchRole(String username);

  @EntityGraph(attributePaths = {})
  Optional<User> findByEmailAndDeletedFalse(String email);

  Page<User> findAllByDeletedFalse(Pageable pageable);

  boolean existsByUsernameAndDeletedFalse(String username);

  boolean existsByEmailAndDeletedFalse(String email);

  @Query("select u.tokenVersion from User u where u.id=:id")
  Optional<Integer> findTokenVersionById(Long id);

  @EntityGraph(attributePaths = {})
  Optional<User> findByIdAndDeletedFalse(Long id);
}
