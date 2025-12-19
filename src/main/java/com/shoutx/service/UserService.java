package com.shoutx.service;

import com.shoutx.model.User;
import com.shoutx.model.UserMedia;
import com.shoutx.repository.UserRepository;
import com.shoutx.repository.UserMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMediaRepository mediaRepository;

    public User createUser(String username, String email, String name, String instagramId) {
        User user = User.builder()
                .username(username)
                .email(email)
                .name(name)
                .instagramId(instagramId)
                .planType(User.PlanType.BASIC)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByInstagramId(String instagramId) {
        return userRepository.findByInstagramId(instagramId);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User updateUserProfile(User user, String name, String profileImageUrl, Boolean isVerified) {
        user.setName(name);
        user.setProfileImageUrl(profileImageUrl);
        user.setIsInstagramVerified(isVerified);
        return userRepository.save(user);
    }

    public User updateFollowerCount(User user, Long followerCount) {
        user.setFollowerCount(followerCount);
        return userRepository.save(user);
    }

    public User upgradeToPro(User user) {
        user.setPlanType(User.PlanType.PRO);
        return userRepository.save(user);
    }

    public User downgradeToBadge(User user) {
        user.setPlanType(User.PlanType.BASIC);
        return userRepository.save(user);
    }

    public UserMedia addMedia(User user, String mediaUrl, UserMedia.MediaType mediaType, Boolean isFromInstagram) {
        // Check if user already has max media (3)
        long mediaCount = mediaRepository.countByUser(user);
        if (mediaCount >= 3) {
            throw new RuntimeException("Maximum 3 photos allowed");
        }

        UserMedia media = UserMedia.builder()
                .user(user)
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .isFromInstagram(isFromInstagram)
                .build();

        return mediaRepository.save(media);
    }

    public void deleteMedia(User user, Long mediaId) {
        UserMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (!media.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You can only delete your own media");
        }

        long mediaCount = mediaRepository.countByUser(user);
        if (mediaCount <= 1) {
            throw new RuntimeException("You must have at least 1 photo. Cannot delete last photo.");
        }

        mediaRepository.delete(media);
    }

    public List<UserMedia> getUserMedia(User user) {
        return mediaRepository.findByUser(user);
    }

    public void addRating(User user, Double rating) {
        if (rating < 0 || rating > 5) {
            throw new RuntimeException("Rating must be between 0 and 5");
        }

        int totalRatings = user.getTotalRatings();
        Double currentAverage = user.getRating() * totalRatings;
        Double newAverage = (currentAverage + rating) / (totalRatings + 1);

        user.setRating(newAverage);
        user.setTotalRatings(totalRatings + 1);
        userRepository.save(user);
    }

    public List<User> searchUsersByFollowerRange(Long minFollowers, Long maxFollowers) {
        return userRepository.findUsersByFollowerRange(minFollowers, maxFollowers);
    }

    public List<User> getActiveUsers() {
        return userRepository.findActiveUsersSortedByFollowers();
    }

    public List<User> getProUsers() {
        return userRepository.findProUsers();
    }

    public void banAccount(User user) {
        user.setIsAccountBanned(true);
        user.setIsAccountDeleted(true);
        userRepository.save(user);
    }
}
