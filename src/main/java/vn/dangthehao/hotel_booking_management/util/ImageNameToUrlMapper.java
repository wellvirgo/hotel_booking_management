package vn.dangthehao.hotel_booking_management.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImageNameToUrlMapper {
    @Value("${base_url}")
    private String baseUrl;

    public List<String> toUrls(List<String> imageFileNames, String archiveFolderName) {
        List<String> imageUrls = new ArrayList<>();
        for (String imageFileName : imageFileNames) {
            imageUrls.add(toUrl(imageFileName, archiveFolderName));
        }

        return imageUrls;
    }

    public String toUrl(String imageFileName, String archiveFolderName) {
        return String.format("%s/%s/%s", this.baseUrl, archiveFolderName, imageFileName);
    }
}
