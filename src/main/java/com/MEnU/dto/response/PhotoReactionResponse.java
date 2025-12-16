package com.MEnU.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoReactionResponse {
    private Long userId;//người react
    private Long photoId;
    private Set<String> emoji;
}
