package com.shoutx.repository;

import com.shoutx.model.User;
import com.shoutx.model.UserStrike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserStrikeRepository extends JpaRepository<UserStrike, Long> {

    List<UserStrike> findByUser(User user);
    long countByUser(User user);
}
