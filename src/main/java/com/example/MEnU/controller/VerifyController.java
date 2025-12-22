package com.example.MEnU.controller;

import com.example.MEnU.exception.BadRequestException;
import com.example.MEnU.exception.ResourceConflictException;
import com.example.MEnU.exception.UnauthorizedException;
import com.example.MEnU.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Verify Service")
@SecurityRequirement(name = "bearerAuth")
@Controller
@RequestMapping("/api/auth")
public class VerifyController {
  private final AuthService authService;

  public VerifyController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "Api khi bấm vào link verify mật khẩu mới trong mail sẽ trả ra trang web")
  @GetMapping("/verify")
  public String verify(@RequestParam String token, Model model) {
    try {
      authService.verify(token);
      model.addAttribute("icon", "✔");
      model.addAttribute("title", "Verified successfully");
      model.addAttribute(
          "message", "Your account has been verified successfully.<br/>You can now log in.");

    } catch (BadRequestException e) {
      model.addAttribute("icon", "✖");
      model.addAttribute("title", "Invalid Verification Link");
      model.addAttribute("message", "The verification link is invalid.");
    } catch (ResourceConflictException e) {
      model.addAttribute("icon", "✔");
      model.addAttribute("title", "Account already verified");
      model.addAttribute(
          "message", "Your account has been already verified before.<br/>You can now log in.");
    } catch (UnauthorizedException e) {
      model.addAttribute("icon", "✖");
      model.addAttribute("title", "Expired Verification Link");
      model.addAttribute("message", "The verification link is expired.");
    }

    return "email/verify-result";
  }
}
