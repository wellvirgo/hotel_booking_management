package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UploadFileService {
    @NonFinal
    @Value("${file.upload_folder}")
    String baseUploadFolder;

    @NonFinal
    @Value("${base_url}")
    String baseUrl;

    private String createFolderIfNotExists(String targetFolderName) {
        String targetFolderPath = baseUploadFolder + targetFolderName;
        File targetFolder = new File(targetFolderPath);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        return targetFolder.getPath();
    }

    public String saveFile(String targetFolderName, MultipartFile uploadFile) {
        if (uploadFile.isEmpty())
            return "";
        String targetFolderPath = createFolderIfNotExists(targetFolderName);
        String uploadFileName = System.currentTimeMillis() + "_" + uploadFile.getOriginalFilename();
        Path uploadFilePath = Paths.get(targetFolderPath, uploadFileName);
        try {
            Files.write(uploadFilePath, uploadFile.getBytes());
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_UPLOAD_FILE);
        }

        return normalizeFilePath(uploadFilePath.toString());
    }

    private String normalizeFilePath(String filePath) {
        filePath = filePath
                .replace("\\", "/")
                .replace(baseUploadFolder, "");

        if (!filePath.startsWith("/"))
            filePath = "/" + filePath;
        return baseUrl + filePath;
    }
}
