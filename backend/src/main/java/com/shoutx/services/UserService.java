package com.shoutx.services;

import com.shoutx.models.User;
import com.shoutx.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    public User updateUser(Long id, User updatedUser) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setFollowers(updatedUser.getFollowers());
            user.setRating(updatedUser.getRating());
            user.setStrikes(updatedUser.getStrikes());
            return userRepository.save(user);
        }
        return null;
    }

    public List<User> getFeaturedCreators(int limit) {
        return userRepository.findFeaturedCreators(limit);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }
}
