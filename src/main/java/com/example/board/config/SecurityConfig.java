package com.example.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // UserDetailsService(UserService)는 Spring Boot가 자동으로
    // AuthenticationManager에 연결하여 사용합니다.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // 메인, 로그인/회원가입, 정적 리소스 모두 허용
                        .requestMatchers("/", "/user/login", "/user/register", "/static/**", "/css/**", "/js/**").permitAll()
                        // /posts 및 하위 경로 모두 허용
                        .requestMatchers("/posts", "/posts/**").permitAll()
                        // /user/** 경로 인증 필요 (마이페이지, 비밀번호 변경)
                        .requestMatchers("/user/**").authenticated()
                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/user/login")       // 커스텀 로그인 페이지 URL
                        .loginProcessingUrl("/user/login") // 로그인 폼 제출 URL

                        // [수정됨] 로그인 폼에서 사용할 파라미터 이름을 'username'에서 'user_id'로 변경
                        .usernameParameter("user_id")

                        .passwordParameter("password") // 비밀번호 파라미터 이름
                        .defaultSuccessUrl("/posts", true) // 로그인 성공 시 /posts로 리다이렉트
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")      // 로그아웃 성공 시 루트 페이지로
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

