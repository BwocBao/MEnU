package com.MEnU.service.Impl;

import com.MEnU.dto.request.CommentRequest;
import com.MEnU.dto.response.MessageResponse;
import com.MEnU.entity.Message;
import com.MEnU.entity.Photo;
import com.MEnU.entity.User;
import com.MEnU.exception.BadRequestException;
import com.MEnU.exception.UnauthorizedException;
import com.MEnU.repository.MessageRepository;
import com.MEnU.repository.PhotoRepository;
import com.MEnU.service.AuthService;
import com.MEnU.service.CloudinaryService;
import com.MEnU.service.FriendService;
import com.MEnU.service.PhotoService;
import com.MEnU.websocket.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;
    private final CloudinaryService cloudinaryService;
    private final AuthService authService;
    private final FriendService friendService;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Override
    @Transactional
    public Photo createPhoto(String caption, MultipartFile image) {

        User currentUser = authService.getCurrentUser();

        // Upload lên Cloudinary
        String imageURL = cloudinaryService.upload(image);

        Photo photo = new Photo();
        photo.setOwner(currentUser);
        photo.setCaption(caption);
        photo.setImageURL(imageURL);

        return photoRepository.save(photo);
    }

    @Override
    public Photo move(User viewer, Long currentPhotoId, String direction) {
        Photo current = photoRepository.findById(currentPhotoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        // Lấy danh sách ID của viewer + bạn bè
        List<Long> visibleUserIds = friendService.getVisibleUserIds(viewer.getId());

        if (direction.equals("up")) {
            return photoRepository.findTopByOwnerIdInAndCreatedAtGreaterThanOrderByCreatedAtAsc(
                    visibleUserIds, current.getCreatedAt()
            ).orElse(null);
        } else {
            return photoRepository.findTopByOwnerIdInAndCreatedAtLessThanOrderByCreatedAtDesc(
                    visibleUserIds, current.getCreatedAt()
            ).orElse(null);
        }
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId) {
        User currentUser = authService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new BadRequestException("Photo not found"));

        if (!photo.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You do not have permission to delete this photo");
        }

        photoRepository.delete(photo);
    }

    @Override
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Photo not found"));
    }

    @Override
    public byte[] downloadPhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> new BadRequestException("Photo not found"));

        if (photo.getImageURL() == null) {
            throw new RuntimeException("Photo URL not found");
        }

        try {
            URL url = new URL(photo.getImageURL());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (InputStream is = url.openStream()) {
                is.transferTo(baos);
            }

            return baos.toByteArray();

        } catch (Exception e) {
            throw new BadRequestException("Failed to download image");
        }
    }

    @Override
    public void commentOnPhoto(Long photoId, CommentRequest req) {

        User sender = authService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        User receiver = photo.getOwner();

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setText(req.getComment());
        msg.setPhotoId(photoId);

        messageRepository.save(msg);

        MessageResponse res = new MessageResponse();
        res.setType("comment");
        res.setFromUsername(sender.getUsername());
        res.setToUsername(receiver.getUsername());
        res.setPhotoId(photoId);
        res.setContent(req.getComment());
        res.setCreatedAt(msg.getCreatedAt());

        try {
            String json = objectMapper.writeValueAsString(res);
            WebSocketHandler.sendToUser(receiver.getUsername(), json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send realtime message");
        }
    }



}
