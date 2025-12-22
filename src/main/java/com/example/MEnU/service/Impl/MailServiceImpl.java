package com.example.MEnU.service.Impl;

import com.example.MEnU.entity.User;
import com.example.MEnU.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class MailServiceImpl implements MailService {
  private final SpringTemplateEngine templateEngine;
  // Spring tự inject JavaMailSender (bean có sẵn)
  private final JavaMailSender mailSender;

  // Lấy mailFrom từ application.properties
  @Value("${spring.mail.from}")
  private String mailFrom;

  public MailServiceImpl(SpringTemplateEngine templateEngine, JavaMailSender mailSender) {
    this.templateEngine = templateEngine;
    this.mailSender = mailSender;
  }

  /** Gửi email kèm file đính kèm */
  @Async
  @Override
  public void sendMail(
      String recipients, // danh sách email nhận
      String subject, // tiêu đề
      String content, // nội dung (HTML)
      MultipartFile[] files // file đính kèm (tuỳ chọn)
      ) {
    try {

      // Tạo email dạng MIME (hỗ trợ HTML + file)
      MimeMessage message = mailSender.createMimeMessage();

      // true = sử dụng multipart email để attach file
      // "UTF-8" để hỗ trợ tiếng Việt
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      // ---- Người gửi ----
      // mailFrom lấy từ cấu hình
      // "Bwoc Bảo" là tên hiển thị (có thể thay đổi)
      helper.setFrom(mailFrom, "Bwoc Bảo");

      // ---- Người nhận ----
      helper.setTo(recipients);

      // ---- File đính kèm ----
      if (files != null) {
        for (MultipartFile file : files) {
          // file.getOriginalFilename() = tên file FE gửi lên
          helper.addAttachment(file.getOriginalFilename(), file);
        }
      }

      // ---- Tiêu đề ----
      helper.setSubject(subject);

      // ---- Nội dung ----
      // html = true → cho phép gửi nội dung HTML
      helper.setText(content, true);

      // ---- Gửi email ----
      mailSender.send(message);

    } catch (Exception e) {
      // throw runtime để controller tự nhận lỗi
      throw new RuntimeException("Lỗi khi gửi mail: " + e.getMessage(), e);
    }
  }

  @Override
  public String buildVerifyEmail(User user, String link) {
    Context ctx = new Context();
    ctx.setVariable("fullname", user.getDisplayName());
    ctx.setVariable("verifyLink", link);
    return templateEngine.process("email/verify", ctx);
  }

  @Override
  public String buildResetPasswordEmail(User user, String link) {
    Context ctx = new Context();
    ctx.setVariable("fullname", user.getDisplayName());
    ctx.setVariable("resetLink", link);
    return templateEngine.process("email/resetPassword", ctx);
  }

  @Override
  public String buildFeedbackEmail(String username, String email, String message) {
    Context ctx = new Context();
    ctx.setVariable("username", username);
    ctx.setVariable("email", email);
    ctx.setVariable("mess", message);
    return templateEngine.process("email/feedback", ctx);
  }
}
