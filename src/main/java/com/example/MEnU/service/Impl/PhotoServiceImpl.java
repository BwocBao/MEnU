package com.example.MEnU.service.Impl;

import com.example.MEnU.dto.request.CommentRequest;
import com.example.MEnU.dto.response.MessageResponse;
import com.example.MEnU.dto.response.PhotoReactionResponse;
import com.example.MEnU.dto.response.PhotoResponse;
import com.example.MEnU.entity.Message;
import com.example.MEnU.entity.Photo;
import com.example.MEnU.entity.Reaction;
import com.example.MEnU.entity.User;
import com.example.MEnU.exception.BadRequestException;
import com.example.MEnU.exception.UnauthorizedException;
import com.example.MEnU.mapper.PhotoMapper;
import com.example.MEnU.repository.MessageRepository;
import com.example.MEnU.repository.PhotoRepository;
import com.example.MEnU.repository.ReactionRepository;
import com.example.MEnU.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
  private final PhotoRepository photoRepository;
  private final CloudinaryService cloudinaryService;
  private final AuthService authService;
  private final FriendService friendService;
  private final MessageRepository messageRepository;
  private final ObjectMapper objectMapper;
  private final RealtimeService realtimeService;
  private final ReactionRepository reactionRepository;

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
  public PhotoResponse getTopPhoto() {

    User currentUser = authService.getCurrentUser();

    // danh sách user được phép thấy (bạn + chính mình)
    List<Long> visibleUserIds = friendService.getVisibleUserIds(currentUser.getId());

      return photoRepository
              .findTopByOwnerIdInOrderByCreatedAtDesc(visibleUserIds)
              .map(PhotoMapper::toPhotoResponse)
              .orElse(null);
  }

  @Override
  public PhotoResponse move(User viewer, Long currentPhotoId, String direction) {
    Photo current =
        photoRepository
            .findById(currentPhotoId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

    // Lấy danh sách ID của viewer + bạn bè
    List<Long> visibleUserIds = friendService.getVisibleUserIds(viewer.getId());

    if ("up".equals(direction)) {
      return photoRepository
          .moveUp(visibleUserIds, current.getCreatedAt(), current.getId()).map(PhotoMapper::toPhotoResponse)
          .orElse(null);
    } else {
      return photoRepository
          .moveDown(visibleUserIds, current.getCreatedAt(), current.getId()).map(PhotoMapper::toPhotoResponse)
          .orElse(null);
    }
    //        if (direction.equals("up")) {
    //            return
    // photoRepository.findTopByOwnerIdInAndCreatedAtGreaterThanOrderByCreatedAtAsc(
    //                    visibleUserIds, current.getCreatedAt()
    //            ).orElse(null);
    //        } else {
    //            return photoRepository.findTopByOwnerIdInAndCreatedAtLessThanOrderByCreatedAtDesc(
    //                    visibleUserIds, current.getCreatedAt()
    //            ).orElse(null);
    //        }
  }

  @Override
  @Transactional
  public void deletePhoto(Long photoId) {
    User currentUser = authService.getCurrentUser();

    Photo photo =
        photoRepository
            .findById(photoId)
            .orElseThrow(() -> new BadRequestException("Photo not found"));

    if (!photo.getOwner().getId().equals(currentUser.getId())) {
      throw new UnauthorizedException("You do not have permission to delete this photo");
    }

    photoRepository.delete(photo);
  }

  @Override
  public Photo getPhotoById(Long id) {
    return photoRepository
        .findById(id)
        .orElseThrow(() -> new BadRequestException("Photo not found"));
  }

  @Override
  public byte[] downloadPhoto(Long photoId) {
    Photo photo =
        photoRepository
            .findById(photoId)
            .orElseThrow(() -> new BadRequestException("Photo not found"));

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

    Photo photo =
        photoRepository
            .findById(photoId)
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
      realtimeService.sendToAUser(receiver.getUsername(), json);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send realtime message");
    }
  }

  @Override
  public String getAPhoto(Long photoId) {
    Photo photo =
        photoRepository
            .findById(photoId)
            .orElseThrow(() -> new BadRequestException("Photo not found"));
    return photo.getImageURL();
  }

  @Override
  public List<PhotoReactionResponse> getReactionPhoto(Long photoId) {

    List<Reaction> reactions = reactionRepository.findAllByPhotoId(photoId);

    // Map<userId, Set<emoji>>
    Map<Long, Set<String>> map = new HashMap<>();

    for (Reaction r : reactions) {

      Long userId = r.getUser().getId();
      String emoji = r.getEmoji();

      map.computeIfAbsent(userId, k -> new HashSet<>()) // Nếu chưa có key → tạo value mới
          .add(emoji); // Nếu đã có key → lấy value hiện có và add value vào
    }

    //        if (!map.containsKey(userId)) {
    //            map.put(userId, new HashSet<>());
    //        }
    //
    //        map.get(userId).add(emoji);

    // Map → DTO
    List<PhotoReactionResponse> result = new ArrayList<>();

    for (Map.Entry<Long, Set<String>> entry : map.entrySet()) {
      result.add(new PhotoReactionResponse(entry.getKey(), photoId, entry.getValue()));
    }

    return result;
  }
}
