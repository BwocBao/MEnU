package com.MEnU.service;

import com.MEnU.dto.request.CommentRequest;
import com.MEnU.dto.response.PhotoReactionResponse;
import com.MEnU.entity.Photo;
import com.MEnU.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {
    Photo createPhoto(String caption, MultipartFile image);
    Photo getTopPhoto();
    Photo move(User viewer, Long currentPhotoId, String direction);
    void deletePhoto(Long photoId);
    Photo getPhotoById(Long id);
    byte[] downloadPhoto(Long photoId);
    void commentOnPhoto(Long photoId, CommentRequest request);
    Photo getAPhoto(Long photoId);
    List<PhotoReactionResponse> getReactionPhoto(Long photoId);
}
