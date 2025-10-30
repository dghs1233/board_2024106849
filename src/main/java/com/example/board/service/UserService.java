package com.example.board.service;

import com.example.board.model.User;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
// [추가됨] 비활성화된 계정 예외
import org.springframework.security.authentication.DisabledException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param userId 가입할 ID
     * @param password 가입할 비밀번호
     * @return 저장된 User 객체
     */
    @Transactional
    public User register(String userId, String password) {
        // 1. ID 중복 체크
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalStateException("이미 사용 중인 ID입니다: " + userId);
        }

        // 2. 사용자 생성
        User user = new User();
        user.setUserId(userId);
        // [수정됨] 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(password));
        // [수정됨] DB 스키마에 맞게 기본값 설정
        user.setCreatedAt(LocalDateTime.now());
        user.setAdmin(false); // 기본값 false
        user.setDel(false);   // 기본값 false

        // 3. 저장
        return userRepository.save(user);
    }

    /**
     * Spring Security가 로그인 시 호출하는 메서드
     * @param userId (SecurityConfig에서 "user_id" 파라미터로 지정함)
     * @return UserDetails 객체 (Spring Security가 내부적으로 사용)
     * @throws UsernameNotFoundException, DisabledException
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 1. userId로 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // [★추가됨] 탈퇴(is_del = true) 여부 확인
        if (user.isDel()) {
            // 탈퇴한 계정일 경우 DisabledException 발생
            throw new DisabledException("탈퇴 처리된 계정입니다: " + userId);
        }

        // 2. Spring Security가 사용할 UserDetails 객체로 변환하여 반환
        // (권한은 현재 "ROLE_USER" 또는 "ROLE_ADMIN" 등으로 관리하지 않고 있음)
        // (만약 admin 필드를 사용하려면 "ROLE_ADMIN" 권한을 추가해야 함)
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                Collections.emptyList() // 현재는 권한(Role)을 사용하지 않음
        );
    }
}

