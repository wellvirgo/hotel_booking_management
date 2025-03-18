package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserCrtResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserListResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserUpdateResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.UserMapper;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.RoleRepository;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    UploadFileService uploadFileService;

    @NonFinal
    @Value("${base_url}")
    String baseUrl;

    @NonFinal
    @Value(("${file.user_avatar_folder_name}"))
    String avatarFolderName;

    @NonFinal
    private String status = "Success";

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public ApiResponse<UserCrtResponse> create(UserCrtRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);
        userRepository.save(user);
        UserCrtResponse userCrtResponse = userMapper.toUserCrtResponse(user);
        userCrtResponse.setRoleName(role.getRoleName());

        return ApiResponse.<UserCrtResponse>builder()
                .status(status)
                .code(HttpStatus.CREATED.value())
                .message("User is created")
                .data(userCrtResponse)
                .build();
    }

    public ApiResponse<UserUpdateResponse> updateAccountInf(
            User currentUser,
            UserUpdateRequest request,
            MultipartFile file) {
        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());
        currentUser.setPhone(request.getPhone());
        String avatar = uploadFileService.saveFile(avatarFolderName, file);
        String avatarFileName = avatar.replace(baseUrl + "/" + avatarFolderName, "");
        currentUser.setAvatar(avatarFileName);
        UserUpdateResponse userUpdateResponse = userMapper.toUserUpdateResponse(userRepository.save(currentUser));
        userUpdateResponse.setAvatar(avatar);

        return ApiResponse.<UserUpdateResponse>builder()
                .status(status)
                .code(HttpStatus.OK.value())
                .message("User is updated")
                .data(userUpdateResponse)
                .build();
    }

    public ApiResponse<List<UserListResponse>> listUsers() {
        List<User> userList = userRepository.findAll();
        List<UserListResponse> userListResponse = userList.stream()
                .map(user -> {
                    UserListResponse userLResponse = userMapper.toUserListResponse(user);
                    String avatar = (user.getAvatar() != null)
                            ? baseUrl + "/" + avatarFolderName + user.getAvatar()
                            : "";
                    userLResponse.setAvatar(avatar);
                    userLResponse.setRoleName(user.getRole().getRoleName());
                    return userLResponse;
                })
                .toList();

        return ApiResponse.<List<UserListResponse>>builder()
                .status(status)
                .code(HttpStatus.OK.value())
                .message("List users successfully")
                .data(userListResponse)
                .build();
    }
}
