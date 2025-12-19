package com.shoutx.service;

import com.shoutx.entity.User;
import com.shoutx.entity.UserPhoto;
import com.shoutx.repository.UserPhotoRepository;
import com.shoutx.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPhotoService {
    
    private final UserPhotoRepository userPhotoRepository;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final S3Service s3Service;
    
    @Transactional
    public UserPhoto uploadPhoto(Long userId, MultipartFile file) throws IOException {
        User user = userService.getUserById(userId);
        
        // Validate file
        if (file.isEmpty() || file.getSize() == 0) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("File size exceeds 10MB");
        }
        
        // Check photo limit (max 3)
        long photoCount = userPhotoRepository.countByUserId(userId);
        if (photoCount >= 3) {
            throw new IllegalArgumentException("Maximum 3 photos allowed per user");
        }
        
        // Upload to S3
        String photoKey = "photos/" + userId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        String photoUrl = s3Service.uploadFile(file, photoKey);
        
        // Save to database
        UserPhoto photo = UserPhoto.builder()
                .user(user)
                .photoUrl(photoUrl)
                .photoKey(photoKey)
                .isFromInstagram(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        return userPhotoRepository.save(photo);
    }
    
    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        UserPhoto photo = userPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        
        if (!photo.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to delete this photo");
        }
        
        // Check if user has more than 1 photo
        long photoCount = userPhotoRepository.countByUserId(userId);
        if (photoCount <= 1) {
            throw new IllegalArgumentException("Cannot delete the last photo. User must have at least 1 photo.");
        }
        
        // Delete from S3
        if (photo.getPhotoKey() != null) {
            s3Service.deleteFile(photo.getPhotoKey());
        }
        
        // Delete from database
        userPhotoRepository.deleteByUserIdAndId(userId, photoId);
        log.info("Photo {} deleted for user {}", photoId, userId);
    }
    
    public List<UserPhoto> getUserPhotos(Long userId) {
        return userPhotoRepository.getUserPhotosOrderByNewest(userId);
    }
    
    public UserPhoto getPhotoById(Long photoId) {
        return userPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
    }
    
    public long getUserPhotoCount(Long userId) {
        return userPhotoRepository.countByUserId(userId);
    }
}
