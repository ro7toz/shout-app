package com.shout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDto {
    @NotBlank(message = "Username is required")
    private String targetUsername;
    
    @NotBlank(message = "Post link is required")
    @Pattern(regexp = "^(https?://)?(www\\.)?(instagram\\.com/p/|instagram\\.com/reel/)[A-Za-z0-9_-]+", message = "Must be a valid Instagram post or reel URL")
    private String postLink;
}
