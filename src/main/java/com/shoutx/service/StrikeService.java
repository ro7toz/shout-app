package com.shoutx.service;

import com.shoutx.entity.Strike;
import com.shoutx.entity.User;
import com.shoutx.repository.StrikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StrikeService {
    
    private final StrikeRepository strikeRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    
    @Transactional
    public Strike addStrike(Long userId, String reason, Long requestId) {
        User user = userService.getUserById(userId);
        
        Strike strike = Strike.builder()
                .user(user)
                .reason(reason)
                .strikeCount(1)
                .createdAt(LocalDateTime.now())
                .build();
        
        strikeRepository.save(strike);
        
        // Check if user should be banned (3 strikes)
        Integer strikeCount = strikeRepository.getStrikeCountForUser(userId);
        
        if (strikeCount >= 3) {
            log.warn("User {} reached 3 strikes. Banning account.", userId);
            userService.banUser(userId, "Account banned due to 3 strikes");
            notificationService.sendAccountBannedNotification(user);
        } else if (strikeCount >= 2) {
            notificationService.sendStrikeWarningNotification(user, strikeCount);
        }
        
        log.info("Strike added to user {} for reason: {}", userId, reason);
        return strike;
    }
    
    public Integer getStrikeCountForUser(Long userId) {
        return strikeRepository.getStrikeCountForUser(userId);
    }
    
    public List<Strike> getUserStrikes(Long userId) {
        return strikeRepository.getUserStrikesOrderByNewest(userId);
    }
}
