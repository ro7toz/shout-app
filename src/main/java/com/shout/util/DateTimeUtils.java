package com.shout.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public static String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        if (hours < 24) return hours + "h ago";
        if (days < 7) return days + "d ago";
        if (days < 30) return (days / 7) + "w ago";
        if (days < 365) return (days / 30) + "mo ago";
        
        return (days / 365) + "y ago";
    }

    public static long getHoursRemaining(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = startTime.plusHours(24);
        
        long hours = ChronoUnit.HOURS.between(now, expiryTime);
        return Math.max(0, hours);
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMAT) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "";
    }

    public static boolean isExpired(LocalDateTime acceptedAt) {
        if (acceptedAt == null) return false;
        
        LocalDateTime expiryTime = acceptedAt.plusHours(24);
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
