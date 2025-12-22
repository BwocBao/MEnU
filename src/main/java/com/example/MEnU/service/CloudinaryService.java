package com.example.MEnU.service;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
  String upload(MultipartFile file);

  Map uploadRaw(MultipartFile file);
}
