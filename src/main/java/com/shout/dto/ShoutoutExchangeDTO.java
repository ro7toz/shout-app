package com.shout.dto;

import com.shout.model.ShoutoutExchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoutoutExchangeDTO {
    private Long id;
    private String requesterUsername;
    private String acceptorUsername;
    private String mediaType;
    private LocalDateTime expiresAt;
    private String status;
    private Boolean requesterPosted;
    private Boolean acceptorPosted;
    private LocalDateTime requesterPostedAt;
    private LocalDateTime acceptorPostedAt;
    private String postUrl;
}
