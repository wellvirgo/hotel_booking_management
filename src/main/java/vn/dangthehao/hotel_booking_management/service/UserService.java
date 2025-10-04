package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.ImageUrlMapper;
import vn.dangthehao.hotel_booking_management.mapper.UserMapper;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.security.Authorities;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {
  UserRepository userRepository;
  RoleService roleService;
  UploadFileService uploadFileService;
  LogService logService;
  PasswordEncoder passwordEncoder;
  UserMapper userMapper;
  ImageUrlMapper imageUrlMapper;

  @NonFinal
  @Value("${base-url}")
  String baseUrl;

  @NonFinal
  @Value("${file.user-avatar-folder}")
  String avatarFolderName;

  public User getByIdWithRole(Long id) {
    return userRepository
        .findByIdAndDeletedFalseFetchRole(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public User getById(Long id) {
    return userRepository
        .findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public User getByUsernameWithRole(String username) {
    return userRepository
        .findByUsernameAndDeletedFalseFetchRole(username)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public User getByEmail(String email) {
    return userRepository
        .findByEmailAndDeletedFalse(email)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public boolean isEmailExists(String email) {
    return userRepository.existsByEmailAndDeletedFalse(email);
  }

  public User create(UserCrtRequest request, Role role) {
    User user = userMapper.toUser(request);

    user.setRole(role);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user);
  }

  public UserCrtResponse register(UserCrtRequest request) {
    String roleName = request.getRoleName();

    if (!Authorities.canRegister(roleName))
      throw new AppException(ErrorCode.ROLE_NOT_ALLOWED_FOR_REGISTRATION, roleName);

    Role role = roleService.getRoleByName(roleName);

    User savedUser = create(request, role);

    UserCrtResponse userCrtResponse = userMapper.toUserCrtResponse(savedUser);
    userCrtResponse.setRoleName(role.getRoleName());

    return userCrtResponse;
  }

  public UserResponse getCurrentUser(Long userId) {
    User currentUser = getByIdWithRole(userId);
    UserResponse userResponse = userMapper.toUserResponse(currentUser);

    String avatar = imageUrlMapper.toUrl(currentUser.getAvatar(), avatarFolderName);
    userResponse.setAvatar(avatar);
    userResponse.setRoleName(currentUser.getRole().getRoleName());

    return userResponse;
  }

  public UserUpdateResponse updateUserInf(
      Long userId, UserUpdateRequest request, MultipartFile avatarFile) {
    User currentUser = getByIdWithRole(userId);

    currentUser.setFullName(request.getFullName());
    currentUser.setEmail(request.getEmail());
    currentUser.setPhone(request.getPhone());

    String avatar = uploadFileService.saveFile(avatarFolderName, avatarFile);
    String avatarFileName = imageUrlMapper.toImageName(avatar, avatarFolderName);
    currentUser.setAvatar(avatarFileName);

    UserUpdateResponse response = userMapper.toUserUpdateResponse(userRepository.save(currentUser));
    response.setAvatar(avatar);

    return response;
  }

  public void updateTokenVersion(User user) {
    user.setTokenVersion(user.getTokenVersion() + 1);
    userRepository.save(user);
  }

  public void updatePassword(User user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public long getTokenVersion(Long id) {
    return userRepository
        .findTokenVersionById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public Page<User> getUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findAllByDeletedFalse(pageable);
  }

  public String getAvatarUrl(User user) {
    return imageUrlMapper.toUrl(user.getAvatar(), avatarFolderName);
  }

  public void deleteMyAccount(Long userId) {
    User user = getById(userId);
    user.setDeleted(true);
    user.setTokenVersion(user.getTokenVersion() + 1);
    saveUser(user);

    String log = String.format("User with id=%d deleted their account voluntarily", userId);
    logService.logUserManagement(log);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }
}
