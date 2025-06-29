package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.AmenityCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.Amenity;
import vn.dangthehao.hotel_booking_management.repository.AmenityRepository;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AmenityService {
    AmenityRepository amenityRepository;
    ResponseGenerator responseGenerator;

    public ApiResponse<Void> createAmenity(AmenityCrtRequest crtRequest) {
        Optional<Amenity> amenityOptional=amenityRepository.findByName(crtRequest.getName());
        if (amenityOptional.isPresent())
            throw new AppException(ErrorCode.AMENITY_EXISTS);
        Amenity amenity = new Amenity();
        amenity.setName(crtRequest.getName());
        amenityRepository.save(amenity);

        return responseGenerator.generateSuccessResponse("Add a new amenity successfully!");
    }
}
