package com.MEnU.config;


import com.MEnU.dto.ApiResponse;
import com.MEnU.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@EnableMethodSecurity   // nếu bạn dùng @PreAuthorize() ở controller/service
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests
                        -> authorizeRequests.requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                // ===== CUSTOM JSON ERROR RESPONSE =====
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            Object secEx = req.getAttribute("SECURITY_EXCEPTION");

                            res.setContentType("application/json");

                            if (secEx instanceof LockedException) {
                                res.setStatus(403);
                                res.getWriter().write(
                                        objectMapper.writeValueAsString(
                                                ApiResponse.error("Account has been deleted")
                                        )
                                );
                            } else if (secEx instanceof DisabledException) {
                                res.setStatus(403);
                                res.getWriter().write(
                                        objectMapper.writeValueAsString(
                                                ApiResponse.error("Account is not activated")
                                        )
                                );
                            } else {
                                res.setStatus(401);
                                res.getWriter().write(
                                        objectMapper.writeValueAsString(
                                                ApiResponse.error("Unauthorized + Token missing or invalid")
                                        )
                                );
                            }
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //WebSecurityCustomizer bỏ qua toàn bộ Security Filter Chain những request dưới ko cần vào Security Filter Chain
    //Vì nếu cho các request đó vào Security Filter Chain rồi mới thấy permit thì có thể ko hiệu quả hoặc gây ra lỗi
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                // Swagger UI (Springdoc OpenAPI)
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/swagger-initializer/**",
                        "/webjars/**"
                )
                // Actuator (health check)
                .requestMatchers(
                        "/actuator/**"
                )
                // Static resources
                .requestMatchers(
                        "/favicon.ico",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/fonts/**",
                        "/assets/**"
                );
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);//Gắn luật load user của bạn vào hệ thống login của Spring
        provider.setPasswordEncoder(passwordEncoder());//Nó nói với Spring rằng:So sánh password bằng cách nào?
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
