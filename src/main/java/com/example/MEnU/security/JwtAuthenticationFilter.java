package com.example.MEnU.security;

import com.example.MEnU.entity.User;
import com.example.MEnU.entity.enums.TokenType;
import com.example.MEnU.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    try {
//      String username = jwtService.extractUsernameFromAccess(token);
//      if (username != null
//          && SecurityContextHolder.getContext().getAuthentication()
//              == null) { // mới login nên chưa set authentication
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        if (userDetails != null && jwtService.isAccessTokenValid(token, userDetails)) {
//          var authToken =
//              new UsernamePasswordAuthenticationToken(
//                  userDetails, null, userDetails.getAuthorities());
//          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//          SecurityContextHolder.getContext().setAuthentication(authToken);
//        }
//      }
        // Extract username từ access token
        String username = jwtService.extractUsernameFromAccess(token);

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            User userDetails = User.builder()
                    .username(username)
                    .build();
            var authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }

    } catch (Exception e) {
      // security handle
      request.setAttribute("SECURITY_EXCEPTION", e);
    }
    filterChain.doFilter(request, response);
  }
}
