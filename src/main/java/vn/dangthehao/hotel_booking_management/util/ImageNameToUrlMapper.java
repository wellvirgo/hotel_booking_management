package vn.dangthehao.hotel_booking_management.util;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageNameToUrlMapper {
  @Value("${base-url}")
  private String baseUrl;

  public List<String> toUrls(List<String> imageFileNames, String archiveFolderName) {
    if (imageFileNames == null || imageFileNames.isEmpty()) {
      return Collections.emptyList();
    }

    return imageFileNames.stream().map(imgName -> toUrl(imgName, archiveFolderName)).toList();
  }

  public String toUrl(String imageFileName, String archiveFolderName) {
    if (imageFileName == null || imageFileName.isBlank()) {
      return "";
    }

    return String.format("%s/%s/%s", this.baseUrl, archiveFolderName, imageFileName);
  }
}
