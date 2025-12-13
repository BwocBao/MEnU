package com.MEnU.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    String upload(MultipartFile file);
    Map uploadRaw(MultipartFile file);
}
