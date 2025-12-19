package com.shoutx.services;

import com.shoutx.models.Exchange;
import com.shoutx.repositories.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExchangeService {

    @Autowired
    private ExchangeRepository exchangeRepository;

    public Exchange createExchange(Exchange exchange) {
        return exchangeRepository.save(exchange);
    }

    public List<Exchange> getUserExchanges(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var exchanges = exchangeRepository.findByUser1IdOrUser2Id(userId, userId, pageable);
        return exchanges.getContent();
    }

    public Long countUserExchanges(Long userId) {
        return exchangeRepository.countByUserId(userId);
    }

    public Exchange updateExchangeStatus(Long exchangeId, String status) {
        Exchange exchange = exchangeRepository.findById(exchangeId).orElse(null);
        if (exchange != null) {
            exchange.setStatus(status);
            return exchangeRepository.save(exchange);
        }
        return null;
    }
}
