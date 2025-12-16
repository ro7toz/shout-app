package com.shout.util;

import java.util.regex.Pattern;

public class InstagramUtils {
    private static final Pattern INSTAGRAM_POST_PATTERN = 
        Pattern.compile("^(https?://)?(www\\.)?(instagram\\.com/p/|instagram\\.com/reel/)[A-Za-z0-9_-]+(\\/)?$");
    
    private static final Pattern INSTAGRAM_PROFILE_PATTERN = 
        Pattern.compile("^(https?://)?(www\\.)?(instagram\\.com/)[a-zA-Z0-9._-]+(\\/)?$");

    public static boolean isValidInstagramPostUrl(String url) {
        return url != null && INSTAGRAM_POST_PATTERN.matcher(url).matches();
    }

    public static boolean isValidInstagramProfileUrl(String url) {
        return url != null && INSTAGRAM_PROFILE_PATTERN.matcher(url).matches();
    }

    public static String extractPostId(String url) {
        if (!isValidInstagramPostUrl(url)) {
            return null;
        }
        
        String[] parts = url.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ((parts[i].equals("p") || parts[i].equals("reel")) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }

    public static String extractUsername(String url) {
        if (!isValidInstagramProfileUrl(url)) {
            return null;
        }
        
        String[] parts = url.split("/");
        for (String part : parts) {
            if (!part.isEmpty() && !part.equals("https:")
                    && !part.equals("http:")
                    && !part.equals("www.")
                    && !part.equals("instagram.com")) {
                return part;
            }
        }
        return null;
    }
}
