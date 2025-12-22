package com.example.MEnU.security;

import com.example.MEnU.service.JwtService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler handler,
      Map<String, Object> attributes) {
    String token = null;

    // 1. Header
    List<String> authHeaders = request.getHeaders().get("Authorization");
    if (authHeaders != null && !authHeaders.isEmpty()) {
      String h = authHeaders.get(0);
      if (h.startsWith("Bearer ")) {
        token = h.substring(7);
      }
    }

    // 2. Query fallback
    if (token == null) {
      String query = request.getURI().getQuery();
      if (query != null && query.startsWith("token=")) {
        token = query.substring(6);
      }
    }

    if (token == null) return false;

    String username;
    try {
      username = jwtService.extractUsernameFromAccess(token);
    } catch (Exception e) {
      return false;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (!jwtService.isAccessTokenValid(token, userDetails)) {
      return false;
    }

    attributes.put("username", username);
    return true;
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler handler,
      Exception ex) {}
}
