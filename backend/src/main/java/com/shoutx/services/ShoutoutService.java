package com.shoutx.services;

import com.shoutx.models.ShoutoutRequest;
import com.shoutx.repositories.ShoutoutRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShoutoutService {

    @Autowired
    private ShoutoutRequestRepository shoutoutRequestRepository;

    public ShoutoutRequest createRequest(ShoutoutRequest request) {
        return shoutoutRequestRepository.save(request);
    }

    public List<ShoutoutRequest> getTrendingRequests(int limit) {
        return shoutoutRequestRepository.findTrendingRequests(limit);
    }

    public long getTotalExchanges() {
        return shoutoutRequestRepository.count();
    }

    public long getTotalReach() {
        return shoutoutRequestRepository.calculateTotalReach();
    }
}
