package com.khimii.medicalvisitscheduler.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {
    private static final String UTC = "UTC";

    /**
     * Converts a UTC time to a doctor's local time zone.
     *
     * @param utcTime  The date and time in UTC.
     * @param timezone The doctor's time zone identifier (e.g., "America/New_York").
     * @return The converted time as a string in the doctor's local time zone.
     */
    public static String convertUtcToDoctorTimezone(LocalDateTime utcTime, String timezone) {
        return utcTime.atZone(ZoneId.of(UTC))
                .withZoneSameInstant(ZoneId.of(timezone))
                .toLocalDateTime()
                .toString();
    }

    /**
     * Converts a local date and time in a specific time zone to UTC.
     *
     * @param localDateTime The local date and time.
     * @param timezone      The originating time zone.
     * @return The converted date and time in UTC.
     */
    public static LocalDateTime convertToUtcTime(LocalDateTime localDateTime, String timezone) {
        return localDateTime.atZone(ZoneId.of(timezone))
                .withZoneSameInstant(ZoneId.of(UTC))
                .toLocalDateTime();
    }
}
