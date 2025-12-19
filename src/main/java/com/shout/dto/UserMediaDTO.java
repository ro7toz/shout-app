package com.shout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMediaDTO {
    private Long id;
    private String url;
    private String type; // image, video, story
    private String source; // instagram, upload
    private Long createdAt;
}
