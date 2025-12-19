package com.shoutx.services;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {
    public Object getUserById(Long id) {
        return new Object() {
            public Long getId() { return id; }
        };
    }
    public List<?> getFeaturedCreators(int limit) {
        return new ArrayList<>();
    }
    public long getTotalUsers() {
        return 50000L;
    }
}