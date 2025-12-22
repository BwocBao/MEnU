package com.example.MEnU.service.Impl;

import com.cloudinary.Cloudinary;
import com.example.MEnU.exception.BadRequestException;
import com.example.MEnU.service.CloudinaryService;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
  private final Cloudinary cloudinary;

  public CloudinaryServiceImpl(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  @Override
  public String upload(MultipartFile file) {
    try {
      if (file == null || file.isEmpty()) throw new BadRequestException("Avatar missing");

      if (!file.getContentType().startsWith("image"))
        throw new BadRequestException("File is not an image");

      Map uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  Map.of(
                      "folder", "avatars",
                      "resource_type",
                          "image") // Cloudinary sẽ lưu ảnh trong thư mục:Media Library → avatars
                  // resource_type: image Bắt buộc đây là ảnh (png, jpg, jpeg, webp…)
                  // secure_url → gần như bắt buộc dùng
                  // public_id → cực kỳ nên lưu (để xóa/sửa ảnh sau này)
                  );
      return uploadResult.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("Upload image failed", e);
    }
  }

  @Override
  public Map uploadRaw(MultipartFile file) {
    try {
      if (file == null || file.isEmpty()) throw new BadRequestException("Image missing");

      if (!file.getContentType().startsWith("image"))
        throw new BadRequestException("File is not an image");

      return cloudinary
          .uploader()
          .upload(
              file.getBytes(),
              Map.of(
                  "folder", "photos",
                  "resource_type", "image"));

    } catch (IOException e) {
      throw new RuntimeException("Upload failed", e);
    }
  }
}
// cloudinary trả về rất nhiều fields nhưng nên lưu lại secure_url để trả về frontend và public_id
// để khi nào cần thì xóa ảnh
// {
//        "asset_id": "...",
//        "public_id": "avatars/kjs8d9f23",
//        "version": 123456,
//        "format": "jpg",
//        "width": 400,
//        "height": 300,
//        "resource_type": "image",
//        "created_at": "2025-12-03T10:00:00Z",
//        "bytes": 456123,
//        "secure_url": "https://...",
//        "url": "http://..."
// }
