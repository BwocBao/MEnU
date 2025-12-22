package com.example.MEnU.dto.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoReactionResponse {
  private Long userId; // người react
  private Long photoId;
  private Set<String> emoji;
}
