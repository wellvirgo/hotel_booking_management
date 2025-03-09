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
    ApplicationRunner applicationRunner(UserRepository userRepository){
       return args -> {
         if (!userRepository.existsByUsername("admin")){
             Permission permission= Permission.builder()
                     .permissionName(Authorities.ALL_USER)
                     .build();
             Set<Permission> permissions=new HashSet<>();
             permissions.add(permission);
             Role role= Role.builder()
                     .roleName(Authorities.ROLE_ADMIN)
                     .permissions(permissions)
                     .build();

             User admin=User.builder()
                     .username("admin")
                     .password(bCryptPasswordEncoder.encode("admin"))
                     .email("default")
                     .fullName("admin")
                     .role(role)
                     .build();

             userRepository.save(admin);
             log.info("Admin account is created");
         }
       };
    }

}
