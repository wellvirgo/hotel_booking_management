package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.model.Permission;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.PermissionRepository;
import vn.dangthehao.hotel_booking_management.repository.RoleRepository;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.security.Authorities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class DataInitializerService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    RoleService roleService;
    PermissionRepository permissionRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void initializeDefaultData() {
        if (userRepository.existsByUsername("admin"))
            return;

        List<Permission> permissions = createPermissions();
        createRoles(permissions);
        createAdmin();
    }

    public void createAdmin() {
        Role roleAdmin = roleService.findByRoleName(Authorities.ROLE_ADMIN);
        User admin = User.builder()
                .username("admin")
                .password(bCryptPasswordEncoder.encode("admin"))
                .email("admin@gmail.com")
                .fullName("admin")
                .role(roleAdmin)
                .build();

        userRepository.save(admin);
        log.info("Admin is created successfully");
    }

    public List<Permission> createPermissions() {
        Map<String, String> permissionsSpec = new LinkedHashMap<>();
        permissionsSpec.put(Authorities.ALL_USER, "Full control user resource");
        permissionsSpec.put(Authorities.READ_USER, "Only read user resource");
        permissionsSpec.put(Authorities.UPDATE_USER, "Only update user resource");
        permissionsSpec.put(Authorities.DELETE_USER, "Only delete user resource");
        List<Permission> permissions = permissionsSpec.entrySet().stream()
                .map(entry -> buildPermission(entry.getKey(), entry.getValue()))
                .toList();

        return permissionRepository.saveAll(permissions);
    }

    public void createRoles(List<Permission> permissions) {
        Set<Permission> permissionForAdmin = permissions.stream()
                .filter(p -> Authorities.ALL_USER.equals(p.getPermissionName()))
                .collect(Collectors.toSet());

        Set<Permission> permissionsForUser = permissions.stream()
                .filter(p -> permissions.indexOf(p) > 0)
                .collect(Collectors.toSet());
        Role roleAdmin = buildRole(Authorities.ROLE_ADMIN, "Super admin", permissionForAdmin);
        Role roleUser = buildRole(Authorities.ROLE_USER, "Normal user", permissionsForUser);
        Role roleHotelOwner = buildRole(Authorities.ROLE_HOTEL_OWNER, "Hotel owner", permissionsForUser);

        roleRepository.saveAll(List.of(roleAdmin, roleUser, roleHotelOwner));
    }

    private Permission buildPermission(String permissionName, String description) {
        return Permission.builder()
                .permissionName(permissionName)
                .description(description)
                .build();
    }

    private Role buildRole(String roleName, String description, Set<Permission> permissions) {
        return Role.builder()
                .roleName(roleName)
                .description(description)
                .permissions(permissions)
                .build();
    }
}
