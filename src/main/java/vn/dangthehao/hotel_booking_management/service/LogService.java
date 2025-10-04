package vn.dangthehao.hotel_booking_management.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogService {
  static final String LOG_FOLDER_NAME = "logs";
  static final String USER_MANAGEMENT = "userManagementLog.txt";
  static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public synchronized void logUserManagement(String content) {
    if (content == null || content.isBlank()) {
      log.warn("Attempt to log empty content");
      return;
    }

    Path logFilePath = createFileLog(USER_MANAGEMENT);
    content = String.format("[%s] %s%n", getLogTime(), content);

    try {
      Files.writeString(logFilePath, content, StandardOpenOption.APPEND);
    } catch (IOException e) {
      log.error("Failed to write log: {}", content, e);
      throw new RuntimeException("Failed to write log", e);
    }
  }

  private String getLogTime() {
    return LocalDateTime.now().format(FORMATTER);
  }

  private Path createFileLog(String fileName) {
    Path logFolder = Paths.get(LOG_FOLDER_NAME);
    try {
      Files.createDirectories(logFolder);

      Path logFilePath = logFolder.resolve(fileName);
      if (!Files.exists(logFilePath)) {
        Files.createFile(logFilePath);
        log.info("Created file {} in {}", fileName, logFolder.getFileName());
      }

      return logFilePath;
    } catch (IOException e) {
      log.error("Failed to create log file {}", fileName, e);
      throw new IllegalArgumentException("Cannot create log file", e);
    }
  }
}
