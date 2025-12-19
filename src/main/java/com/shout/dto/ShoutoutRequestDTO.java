package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoutoutRequestDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderUsername;
    private String senderProfilePicture;
    private Long receiverId;
    private Long mediaId;
    private String status; // pending, accepted, completed, expired
    private String repostType; // story, post, reel
    private Long createdAt;
    private Long completedAt;
    private Boolean sentByMe;
    private Boolean receivedByMe;
    private Integer hoursLeft;
}
