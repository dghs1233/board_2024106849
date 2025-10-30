package com.example.board.repository;

import com.example.board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // User 엔티티의 'userId' 필드를 기준으로 사용자를 찾도록 메서드 이름 변경
    // (기존: findByUsername)
    Optional<User> findByUserId(String userId);

    // User 엔티티의 'userId' 필드를 기준으로 사용자 존재 여부 확인
    // (기존: existsByUsername)
    boolean existsByUserId(String userId);
}

