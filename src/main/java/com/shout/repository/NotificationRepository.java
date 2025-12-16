package com.shout.repository;

import com.shout.model.Notification;
import com.shout.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser(User user, Pageable pageable);

    List<Notification> findByUserAndIsReadFalse(User user);

    Long countByUserAndIsReadFalse(User user);
}