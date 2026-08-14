package com.example.authservice.utils;

import com.example.library.utils.StringCommon;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final ZoneId ZONE_ID = ZoneId.of(StringCommon.TIME_ZONE_VN);
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+07:00");

    private TimeUtils() {
    }

    public static OffsetDateTime now() {
        return OffsetDateTime.now(ZONE_ID);
    }

    public static LocalDate localDateNow() {
        return LocalDate.now(ZONE_ID);
    }

    public static LocalTime localTimeNow() {
        return LocalTime.now(ZONE_ID);
    }

    public static String dateTimeFormat() {
        return LocalDateTime.now(ZONE_ID).format(DateTimeFormatter.ofPattern(StringCommon.DATE_TIME_FORMAT_NO_TZ));
    }
}
