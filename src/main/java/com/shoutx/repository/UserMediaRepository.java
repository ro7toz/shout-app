package com.shoutx.repository;

import com.shoutx.model.User;
import com.shoutx.model.UserMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserMediaRepository extends JpaRepository<UserMedia, Long> {

    List<UserMedia> findByUser(User user);
    long countByUser(User user);
}
