package vn.dangthehao.hotel_booking_management.locking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LockStrategyFactory {
  Map<String, BookingLockStrategy> lockStrategyMap;

  public LockStrategyFactory(List<BookingLockStrategy> bookingLockStrategies) {
    lockStrategyMap = new HashMap<>();
    for (BookingLockStrategy strategy : bookingLockStrategies) {
      String strategyName = strategy.getClass().getAnnotation(Component.class).value();
      lockStrategyMap.put(strategyName, strategy);
    }
  }

  public BookingLockStrategy getLockStrategy(String strategyName) {
    BookingLockStrategy lockStrategy = lockStrategyMap.get(strategyName);
    if (lockStrategy == null) {
      throw new AppException(ErrorCode.ILLEGAL_LOCK_TYPE, strategyName);
    }

    return lockStrategyMap.get(strategyName);
  }
}
