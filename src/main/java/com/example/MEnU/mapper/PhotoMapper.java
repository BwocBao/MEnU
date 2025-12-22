package com.example.MEnU.mapper;

import com.example.MEnU.dto.response.PhotoResponse;
import com.example.MEnU.entity.Photo;
import com.example.MEnU.entity.User;

public class PhotoMapper {

    public static PhotoResponse toPhotoResponse(Photo photo) {
        if (photo == null) {
            return null;
        }

        User owner = photo.getOwner();

        return new PhotoResponse(
                photo.getId() != null ? photo.getId().toString() : null,
                photo.getCreatedAt() != null ? photo.getCreatedAt() : null,
                owner != null ? owner.getId() : null,
                owner != null ? owner.getDisplayName() : null,
                owner != null ? owner.getAvatarURL() : null,
                photo.getCaption()!=null ? photo.getCaption() : null,
                photo.getImageURL()!=null ? photo.getImageURL() : null
        );
    }

}
