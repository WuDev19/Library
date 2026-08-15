package com.example.bookborrowservice.utils;

import com.example.bookborrowservice.constants.StringCommon;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final ZoneId ZONE_ID = ZoneId.of(StringCommon.TIME_ZONE_VN);

    private TimeUtils() {
    }

    public static String dateTimeFormat() {
        return LocalDateTime.now(ZONE_ID).format(DateTimeFormatter.ofPattern(StringCommon.DATE_TIME_FORMAT_NO_TZ));
    }
}
