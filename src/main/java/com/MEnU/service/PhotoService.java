package com.MEnU.service;

import com.MEnU.dto.request.CommentRequest;
import com.MEnU.entity.Photo;
import com.MEnU.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    Photo createPhoto(String caption, MultipartFile image);
    Photo move(User viewer, Long currentPhotoId, String direction);
    void deletePhoto(Long photoId);
    Photo getPhotoById(Long id);
    byte[] downloadPhoto(Long photoId);
    void commentOnPhoto(Long photoId, CommentRequest request);
}
