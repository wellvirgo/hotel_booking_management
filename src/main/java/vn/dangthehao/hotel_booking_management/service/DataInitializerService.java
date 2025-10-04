package vn.dangthehao.hotel_booking_management.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.model.Permission;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.security.Authorities;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class DataInitializerService {
  UserRepository userRepository;
  RoleService roleService;
  PermissionService permissionService;
  PasswordEncoder passwordEncoder;

  static final String SYSTEM_ADMIN = "System Administrator";

  @Transactional
  public void initializeDefaultData() {
    if (userRepository.existsByUsernameAndDeletedFalse(SYSTEM_ADMIN)) return;

    List<Permission> permissions = permissionService.createPermissions();

    List<Role> roles =
        List.of(
            createAdminRole(permissions),
            createHotelOwnerRole(permissions),
            createCustomerRole(permissions));
    roleService.createRoles(roles);

    Role systemAdminRole = createSystemAdminRole(permissions);
    createSystemAdmin(systemAdminRole);
  }

  public void createSystemAdmin(Role role) {
    User systemAdmin =
        User.builder()
            .username(SYSTEM_ADMIN)
            .password(passwordEncoder.encode(SYSTEM_ADMIN))
            .email("systemadmin@gmail.com")
            .fullName(SYSTEM_ADMIN)
            .role(role)
            .build();

    userRepository.save(systemAdmin);
    log.info("System admin is created successfully");
  }

  private Role createSystemAdminRole(List<Permission> permissions) {
    List<String> needPermissions =
        List.of(Authorities.USER_ALL, Authorities.HOTEL_ALL, Authorities.BOOKING_ALL);

    return roleService.createRole(
        Authorities.SYSTEM_ADMIN,
        "Full access to manage the entire system",
        getPermissionSet(needPermissions, permissions));
  }

  private Role createAdminRole(List<Permission> permissions) {
    List<String> needPermissions =
        List.of(
            Authorities.USER_CREATE,
            Authorities.USER_READ,
            Authorities.USER_UPDATE,
            Authorities.HOTEL_READ,
            Authorities.HOTEL_UPDATE,
            Authorities.BOOKING_READ,
            Authorities.BOOKING_UPDATE);

    return Role.builder()
        .roleName(Authorities.ADMIN)
        .description("Manage users, hotels, and bookings within assigned scope")
        .permissions(getPermissionSet(needPermissions, permissions))
        .build();
  }

  private Role createHotelOwnerRole(List<Permission> permissions) {
    List<String> needPermissions =
        List.of(
            Authorities.USER_READ,
            Authorities.USER_UPDATE,
            Authorities.HOTEL_CREATE,
            Authorities.HOTEL_READ,
            Authorities.HOTEL_UPDATE,
            Authorities.HOTEL_DELETE,
            Authorities.BOOKING_READ,
            Authorities.BOOKING_UPDATE,
            Authorities.BOOKING_CANCEL);

    return Role.builder()
        .roleName(Authorities.HOTEL_OWNER)
        .description("Full control over own hotel and its bookings")
        .permissions(getPermissionSet(needPermissions, permissions))
        .build();
  }

  private Role createCustomerRole(List<Permission> permissions) {
    List<String> needPermissions =
        List.of(
            Authorities.USER_READ,
            Authorities.USER_UPDATE,
            Authorities.USER_DELETE,
            Authorities.HOTEL_READ,
            Authorities.BOOKING_CREATE,
            Authorities.BOOKING_READ,
            Authorities.BOOKING_UPDATE,
            Authorities.BOOKING_CANCEL);

    return Role.builder()
        .roleName(Authorities.CUSTOMER)
        .description("Can book hotels and manage own reservations")
        .permissions(getPermissionSet(needPermissions, permissions))
        .build();
  }

  private Set<Permission> getPermissionSet(
      List<String> permissionNames, List<Permission> permissions) {
    Set<Permission> permissionSet = new HashSet<>();

    for (String permissionName : permissionNames) {
      permissionSet.add(permissionService.getByPermissionName(permissionName, permissions));
    }

    return permissionSet;
  }
}
