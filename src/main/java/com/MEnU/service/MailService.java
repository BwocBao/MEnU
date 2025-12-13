package com.MEnU.service;


import com.MEnU.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface MailService {
    void sendMail(
            String recipients,        // email nhận
            String subject,           // tiêu đề
            String content,           // nội dung (HTML)
            MultipartFile[] files     // file đính kèm (tuỳ chọn)
    );
    String buildVerifyEmail(User user, String link);
    String buildResetPasswordEmail(User user, String link);
}
