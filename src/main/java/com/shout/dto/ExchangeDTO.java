package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeDTO {
    private Long id;
    private Long user1Id;
    private String user1Name;
    private String user1Username;
    private String user1ProfilePicture;
    private Long user2Id;
    private String user2Name;
    private String user2Username;
    private String user2ProfilePicture;
    private String status; // incomplete, complete
    private String timeStatus; // live, expired
    private Long createdAt;
    private Long completedAt;
    private Double rating; // User's rating of the exchange
    private Integer hoursLeft;
    private Boolean isPendingFromMe;
}
