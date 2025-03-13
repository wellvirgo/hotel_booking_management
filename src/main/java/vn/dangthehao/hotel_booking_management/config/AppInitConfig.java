package vn.dangthehao.hotel_booking_management.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.dangthehao.hotel_booking_management.model.Permission;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.PermissionRepository;
import vn.dangthehao.hotel_booking_management.repository.RoleRepository;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.security.Authorities;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppInitConfig {
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                Permission permissionAllUser = buildBasicPermission(Authorities.ALL_USER);
                permissionRepository.save(permissionAllUser);

                Set<Permission> permissionsForAdmin = new HashSet<>();
                permissionsForAdmin.add(permissionAllUser);
                Role roleAdmin = buildBasicRole(Authorities.ROLE_ADMIN, "Senior admin", permissionsForAdmin);

                User admin = User.builder()
                        .username("admin")
                        .password(bCryptPasswordEncoder.encode("admin"))
                        .email("default")
                        .fullName("admin")
                        .role(roleAdmin)
                        .build();
                userRepository.save(admin);
                log.info("Admin account is created");

                Permission permissionReadUser = buildBasicPermission(Authorities.READ_USER);
                permissionRepository.save(permissionReadUser);

                Permission permissionUpdateUser = buildBasicPermission(Authorities.UPDATE_USER);
                permissionRepository.save(permissionUpdateUser);

                Permission permissionDeleteUser = buildBasicPermission(Authorities.DELETE_USER);
                permissionRepository.save(permissionDeleteUser);

                Set<Permission> permissionsForUser = new HashSet<>();
                permissionsForUser.add(permissionReadUser);
                permissionsForUser.add(permissionUpdateUser);
                permissionsForUser.add(permissionDeleteUser);

                Role roleUser = buildBasicRole(Authorities.ROLE_USER, "Normal user", permissionsForUser);
                roleRepository.save(roleUser);

                Role roleHotelOwner = buildBasicRole(
                        Authorities.ROLE_HOTEL_OWNER, "Hotel owner", permissionsForUser);
                roleRepository.save(roleHotelOwner);
            }
        };
    }

    private Permission buildBasicPermission(String permissionName) {
        return Permission.builder()
                .permissionName(permissionName)
                .build();
    }

    private Role buildBasicRole(String roleName, String description, Set<Permission> permissions) {
        return Role.builder()
                .roleName(roleName)
                .description(description)
                .permissions(permissions)
                .build();
    }
}