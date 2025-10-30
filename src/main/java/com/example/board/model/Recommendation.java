package com.example.board.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_recommend", // 사용자가 요청한 테이블 이름
        uniqueConstraints = {
                @UniqueConstraint( // 사용자가 게시글에 중복 추천 방지
                        name = "recommend_uk",
                        columnNames = {"user_id", "board_id"}
                )
        }
)
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 'tbl_recommend'의 'user_id' 컬럼과 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 'tbl_recommend'의 'board_id' 컬럼과 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Post post;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 간단한 생성자
    public Recommendation(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}

