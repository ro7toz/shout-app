package com.shoutx.services;

import com.shoutx.models.MediaItem;
import com.shoutx.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    public List<MediaItem> getUserMedia(Long userId) {
        return mediaRepository.findByUserId(userId);
    }

    public MediaItem addMedia(Long userId, MediaItem mediaItem) {
        mediaItem.setUserId(userId);
        return mediaRepository.save(mediaItem);
    }

    public List<MediaItem> getUserMediaByType(Long userId, String type) {
        return mediaRepository.findByUserIdAndType(userId, type);
    }

    public void deleteMedia(Long mediaId) {
        mediaRepository.deleteById(mediaId);
    }
}
