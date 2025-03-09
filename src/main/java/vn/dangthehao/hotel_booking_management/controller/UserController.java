package vn.dangthehao.hotel_booking_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.hotel_booking_management.security.PermissionChecker;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api")
public class UserController {
    PermissionChecker permissionChecker;

    @GetMapping("/admin/users")
    @PreAuthorize("@permissionChecker.hasAuthorities({'read:user','all:user'})")
    public String getAllUsers(){
        return "Access success";
    }
}
