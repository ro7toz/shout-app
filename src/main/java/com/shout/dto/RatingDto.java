package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Long id;
    private String raterUsername;
    private String ratedUserUsername;
    private Integer score;
    private String reason;
    private String comment;
    private LocalDateTime createdAt;
}
