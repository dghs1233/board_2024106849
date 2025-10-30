package com.example.board.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user") // 사용자가 요청한 테이블 이름
@SQLDelete(sql = "UPDATE tbl_user SET is_del = true WHERE id = ?") // 삭제 요청 시 is_del = true로 업데이트
@Where(clause = "is_del = false") // 조회 시 항상 is_del = false인 것만 조회
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    // 'tbl_user'의 'user_id' 컬럼 (로그인 ID)
    // 기존 'username' 필드를 'userId'로 변경하고 컬럼명 매핑
    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 100) // 비밀번호 (암호화된 상태로 저장됨)
    private String password;

    // 'admin' 컬럼 (관리자 여부)
    // Spring Security의 'ROLE'을 대체
    @Column(name = "admin")
    @ColumnDefault("false")
    private boolean admin = false;

    // 'is_del' 컬럼 (Soft Delete)
    @Column(name = "is_del")
    @ColumnDefault("false")
    private boolean isDel = false;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    // Spring Security에서 사용할 권한 문자열을 반환하는 헬퍼 메서드
    public String getRole() {
        return admin ? "ROLE_ADMIN" : "ROLE_USER";
    }

    // 간단한 회원가입용 생성자 (예시)
    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.admin = false; // 기본값
        this.isDel = false; // 기본값
    }
}

