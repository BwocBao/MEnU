package com.example.MEnU.service;

import com.example.MEnU.dto.request.CommentRequest;
import com.example.MEnU.dto.response.PhotoReactionResponse;
import com.example.MEnU.dto.response.PhotoResponse;
import com.example.MEnU.entity.Photo;
import com.example.MEnU.entity.User;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
  Photo createPhoto(String caption, MultipartFile image);

  PhotoResponse getTopPhoto();

  PhotoResponse move(User viewer, Long currentPhotoId, String direction);

  void deletePhoto(Long photoId);

  Photo getPhotoById(Long id);

  byte[] downloadPhoto(Long photoId);

  void commentOnPhoto(Long photoId, CommentRequest request);

  String getAPhoto(Long photoId);

  List<PhotoReactionResponse> getReactionPhoto(Long photoId);
}
