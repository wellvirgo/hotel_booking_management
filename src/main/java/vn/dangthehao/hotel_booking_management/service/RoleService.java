package vn.dangthehao.hotel_booking_management.service;

import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.model.Permission;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.repository.RoleRepository;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
  RoleRepository roleRepository;

  public Role getByRoleName(String roleName) {
    return roleRepository
        .findByRoleName(roleName)
        .orElseThrow(() -> new RuntimeException("Role not found"));
  }

  public Role getById(Long id) {
    return roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
  }

  public Role getRoleByName(String roleName) {
    return getByRoleName(roleName);
  }

  public Role createRole(String roleName, String description, Set<Permission> permissions) {
    return roleRepository.save(buildRole(roleName, description, permissions));
  }

  public void createRoles(List<Role> roles) {
    roleRepository.saveAll(roles);
  }

  private Role buildRole(String roleName, String description, Set<Permission> permissions) {
    return Role.builder()
        .roleName(roleName)
        .description(description)
        .permissions(permissions)
        .build();
  }
}
