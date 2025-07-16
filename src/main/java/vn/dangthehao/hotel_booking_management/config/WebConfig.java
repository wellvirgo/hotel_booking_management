package vn.dangthehao.hotel_booking_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload_folder}")
    String baseUploadFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + baseUploadFolder + "avatars/");
        registry.addResourceHandler("/roomTypeImages/**")
                .addResourceLocations("file:" + baseUploadFolder + "roomTypeImages/");
        registry.addResourceHandler("/hotelImages/**")
                .addResourceLocations("file:" + baseUploadFolder + "hotelImages/");
    }
}
