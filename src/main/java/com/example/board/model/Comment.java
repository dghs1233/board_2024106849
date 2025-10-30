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
@Table(name = "tbl_comment") // 사용자가 요청한 테이블 이름
@SQLDelete(sql = "UPDATE tbl_comment SET is_del = true WHERE id = ?") // 삭제 요청 시 is_del = true로 업데이트
@Where(clause = "is_del = false") // 조회 시 항상 is_del = false인 것만 조회
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 'tbl_comment'의 'board_id' 컬럼과 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Post post;

    // 'tbl_comment'의 'user_id' 컬럼과 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 댓글 작성자

    @Column(nullable = false, length = 500)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 'is_del' 컬럼 (Soft Delete)
    @Column(name = "is_del")
    @ColumnDefault("false")
    private boolean isDel = false;

    // 'anonymous_id' 컬럼 (익명 ID)
    // CommentService에서 이 값을 설정합니다. (예: 1, 2, 3...)
    @Column(name = "anonymous_id")
    private Integer anonymousId; // '작성자'는 0, 익명은 1부터 시작 등


    // 서비스 레이어에서 익명 ID를 설정하기 위한 생성자 (예시)
    public Comment(Post post, User user, String content, Integer anonymousId) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.anonymousId = anonymousId;
    }
}

