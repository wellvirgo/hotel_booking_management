package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.UserMapper;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.RoleRepository;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

import java.util.List;

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
    ResponseGenerator responseGenerator;

    @NonFinal
    @Value("${base_url}")
    String baseUrl;

    @NonFinal
    @Value(("${file.user_avatar_folder_name}"))
    String avatarFolderName;

    public User findByID(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean checkEmailExist(String email) {
        return userRepository.existsByEmailAndIsDeletedFalse(email);
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

        return responseGenerator.generateSuccessResponse("User is created", userCrtResponse);
    }

    public ApiResponse<UserResponse> getCurrentUser(Jwt jwt) {
        Long userId = jwt.getClaim("userID");
        User currentUser = findByID(userId);
        UserResponse userResponse = userMapper.toUserResponse(currentUser);
        String avatar = (currentUser.getAvatar() != null)
                ? String.format("%s/%s/%s", baseUrl, avatarFolderName, currentUser.getAvatar())
                : "";
        userResponse.setAvatar(avatar);
        userResponse.setRoleName(currentUser.getRole().getRoleName());

        return responseGenerator.generateSuccessResponse(
                "Current user's information", userResponse);
    }

    public ApiResponse<UserUpdateResponse> updateAccountInf(
            Long userID,
            UserUpdateRequest request,
            MultipartFile file) {
        User currentUser = findByID(userID);
        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());
        currentUser.setPhone(request.getPhone());
        String avatar = uploadFileService.saveFile(avatarFolderName, file);
        String avatarFileName = avatar.replace(String.format("%s/%s/", baseUrl, avatarFolderName), "");
        currentUser.setAvatar(avatarFileName);
        UserUpdateResponse userUpdateResponse = userMapper.toUserUpdateResponse(userRepository.save(currentUser));
        userUpdateResponse.setAvatar(avatar);

        return responseGenerator.generateSuccessResponse("User is updated", userUpdateResponse);
    }

    public ApiResponse<List<UserListResponse>> listUsers() {
        List<User> userList = userRepository.findAllByIsDeletedFalse();
        List<UserListResponse> userListResponse = userList.stream()
                .map(this::addAvatarAndRoleName)
                .toList();

        return responseGenerator.generateSuccessResponse("List users successfully!", userListResponse);
    }

    public ApiResponse<Void> deleteByID(Long id) {
        User user = findByID(id);
        user.setDeleted(true);
        userRepository.save(user);

        return responseGenerator.generateSuccessResponse("Delete user successfully!");
    }

    public void saveOrUpdate(User user) {
        userRepository.save(user);
    }

    // Used in list users to map link avatar in server and role name
    private UserListResponse addAvatarAndRoleName(User user) {
        UserListResponse userLResponse = userMapper.toUserListResponse(user);
        String avatar = (user.getAvatar() != null)
                ? String.format("%s/%s/%s", baseUrl, avatarFolderName, user.getAvatar()) : "";
        userLResponse.setAvatar(avatar);
        userLResponse.setRoleName(user.getRole().getRoleName());

        return userLResponse;
    }
}
