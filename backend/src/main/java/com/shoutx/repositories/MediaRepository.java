package com.shoutx.repositories;

import com.shoutx.models.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<MediaItem, Long> {
    List<MediaItem> findByUserId(Long userId);
    List<MediaItem> findByUserIdAndType(Long userId, String type);
}
