package com.shout.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * Instagram Deep Link Utility - Generates Instagram sharing links
 * Supports both deep links for mobile and web fallbacks
 */
@Component
@Slf4j
public class InstagramDeepLinkUtil {
   
    /**
     * Generate Instagram deep link for sharing content
     * Opens Instagram app with pre-filled caption and URL
     */
    public String generateShareLink(String mediaUrl, String caption) {
        try {
            String encodedUrl = URLEncoder.encode(mediaUrl, StandardCharsets.UTF_8.toString());
            String encodedCaption = URLEncoder.encode(caption, StandardCharsets.UTF_8.toString());
           
            String deepLink = String.format(
                "instagram://share?url=%s&caption=%s",
                encodedUrl,
                encodedCaption
            );
           
            log.debug("Generated Instagram deep link for: {}", mediaUrl);
            return deepLink;
           
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding Instagram share link", e);
            return generateWebFallback(mediaUrl);
        }
    }
   
    /**
     * Generate Instagram deep link for story sharing
     * Opens Instagram story creation screen
     */
    public String generateStoryLink(String mediaUrl, String caption) {
        try {
            String encodedUrl = URLEncoder.encode(mediaUrl, StandardCharsets.UTF_8.toString());
            String encodedCaption = URLEncoder.encode(caption, StandardCharsets.UTF_8.toString());
           
            // Story-specific deep link
            String deepLink = String.format(
                "instagram://share?url=%s&caption=%s&media_type=story",
                encodedUrl,
                encodedCaption
            );
           
            log.debug("Generated Instagram story deep link for: {}", mediaUrl);
            return deepLink;
           
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding Instagram story link", e);
            return generateWebFallback(mediaUrl);
        }
    }
   
    /**
     * Generate Instagram deep link for reel sharing
     * Opens Instagram reel creation screen
     */
    public String generateReelLink(String mediaUrl, String caption) {
        try {
            String encodedUrl = URLEncoder.encode(mediaUrl, StandardCharsets.UTF_8.toString());
            String encodedCaption = URLEncoder.encode(caption, StandardCharsets.UTF_8.toString());
           
            // Reel-specific deep link
            String deepLink = String.format(
                "instagram://share?url=%s&caption=%s&media_type=reel",
                encodedUrl,
                encodedCaption
            );
           
            log.debug("Generated Instagram reel deep link for: {}", mediaUrl);
            return deepLink;
           
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding Instagram reel link", e);
            return generateWebFallback(mediaUrl);
        }
    }
   
    /**
     * Generate web fallback link when deep link fails
     * Redirects to Instagram create page
     */
    public String generateWebFallback(String mediaUrl) {
        return "https://www.instagram.com/create/";
    }
   
    /**
     * Generate Instagram DM deep link
     * Opens Instagram to share with a specific user
     */
    public String generateDMLink(String userId) {
        try {
            String encodedUserId = URLEncoder.encode(userId, StandardCharsets.UTF_8.toString());
            String deepLink = String.format("instagram://user?username=%s", encodedUserId);
           
            log.debug("Generated Instagram DM link for user: {}", userId);
            return deepLink;
           
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding Instagram DM link", e);
            return "https://www.instagram.com/";
        }
    }
   
    /**
     * Generate retry posting link
     * When user needs to retry posting for an exchange
     */
    public String generateRetryLink(String exchangeId, String mediaType) {
        try {
            String encodedExchangeId = URLEncoder.encode(exchangeId, StandardCharsets.UTF_8.toString());
           
            // Custom deep link with exchange context
            String deepLink = String.format(
                "instagram://create?exchange_id=%s&media_type=%s",
                encodedExchangeId,
                mediaType
            );
           
            log.debug("Generated retry link for exchange: {} with type: {}", exchangeId, mediaType);
            return deepLink;
           
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding retry link", e);
            return generateWebFallback(null);
        }
    }
   
    /**
     * Check if device can handle Instagram deep links
     * Returns true if likely to be mobile, false if web
     */
    public boolean isDeepLinkSupported(String userAgent) {
        if (userAgent == null) return false;
       
        String lowerUA = userAgent.toLowerCase();
        return lowerUA.contains("mobile") ||
               lowerUA.contains("android") ||
               lowerUA.contains("iphone") ||
               lowerUA.contains("ipad");
    }
}