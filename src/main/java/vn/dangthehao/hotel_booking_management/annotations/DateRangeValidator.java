package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.dangthehao.hotel_booking_management.dto.request.SearchHotelRequest;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, SearchHotelRequest> {
    @Override
    public boolean isValid(SearchHotelRequest request, ConstraintValidatorContext context) {
        LocalDate checkInDate = request.getCheckIn();
        LocalDate checkOutDate = request.getCheckOut();
        if (checkInDate == null || checkOutDate == null)
            return true;

        return checkOutDate.isAfter(checkInDate);
    }
}
