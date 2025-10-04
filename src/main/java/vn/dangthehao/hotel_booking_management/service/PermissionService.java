package vn.dangthehao.hotel_booking_management.service;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.Permission;
import vn.dangthehao.hotel_booking_management.repository.PermissionRepository;
import vn.dangthehao.hotel_booking_management.security.Authorities;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PermissionService {
  PermissionRepository permissionRepository;

  public List<Permission> createPermissions() {
    List<Permission> permissions =
        List.of(
            buildPermission(Authorities.USER_ALL, "Full CRUD permissions on User"),
            buildPermission(Authorities.HOTEL_ALL, "Full CRUD permissions on Hotel"),
            buildPermission(Authorities.BOOKING_ALL, "Full CRUD permission on Booking"),
            buildPermission(Authorities.USER_CREATE, "Only create User"),
            buildPermission(Authorities.USER_READ, "Only read User"),
            buildPermission(Authorities.USER_UPDATE, "Only update User"),
            buildPermission(Authorities.USER_DELETE, "Only delete User"),
            buildPermission(Authorities.HOTEL_CREATE, "Only create Hotel"),
            buildPermission(Authorities.HOTEL_READ, "Only read Hotel"),
            buildPermission(Authorities.HOTEL_UPDATE, "Only update Hotel"),
            buildPermission(Authorities.HOTEL_DELETE, "Only delete Hotel"),
            buildPermission(Authorities.BOOKING_CREATE, "Only create Booking"),
            buildPermission(Authorities.BOOKING_READ, "Only read Booking"),
            buildPermission(Authorities.BOOKING_UPDATE, "Only update Booking"),
            buildPermission(Authorities.BOOKING_CANCEL, "Only cancel Booking"));

    return permissionRepository.saveAll(permissions);
  }

  private Permission buildPermission(String permissionName, String description) {
    return Permission.builder().permissionName(permissionName).description(description).build();
  }

  public Permission getByPermissionName(String permissionName, List<Permission> permissions) {
    return permissions.stream()
        .filter(permission -> permission.getPermissionName().equals(permissionName))
        .findFirst()
        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND, permissionName));
  }
}
