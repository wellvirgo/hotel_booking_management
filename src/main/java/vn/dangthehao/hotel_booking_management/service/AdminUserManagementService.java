package vn.dangthehao.hotel_booking_management.service;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.UserListItemDTO;
import vn.dangthehao.hotel_booking_management.dto.response.UserListResponse;
import vn.dangthehao.hotel_booking_management.mapper.ImageUrlMapper;
import vn.dangthehao.hotel_booking_management.mapper.UserMapper;
import vn.dangthehao.hotel_booking_management.model.User;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AdminUserManagementService {
  UserService userService;
  UserMapper userMapper;
  LogService logService;
  ImageUrlMapper imageUrlMapper;

  @NonFinal
  @Value("${file.user-avatar-folder}")
  String avatarFolderName;

  public UserListResponse listUsers(int page, int size) {
    Page<User> userPage = userService.getUsers(page - 1, size);
    List<User> users = userPage.getContent();
    int totalPages = userPage.getTotalPages();
    int currentPage = userPage.getNumber() + 1;

    List<UserListItemDTO> userList = new ArrayList<>();

    for (User user : users) {
      UserListItemDTO userListItem = userMapper.toUserListItem(user);
      userListItem.setAvatar(imageUrlMapper.toUrl(user.getAvatar(), avatarFolderName));
      userListItem.setRoleName(user.getRole().getRoleName());
      userList.add(userListItem);
    }

    return UserListResponse.builder()
        .userList(userList)
        .currentPage(currentPage)
        .totalPages(totalPages)
        .build();
  }

  public void deleteUser(Long userId, Long adminId) {
    User user = userService.getById(userId);
    user.setDeleted(true);
    user.setTokenVersion(user.getTokenVersion() + 1);
    userService.saveUser(user);

    String log = String.format("Admin with id=%d deleted user with id=%d", adminId, userId);
    logService.logUserManagement(log);
  }
}
