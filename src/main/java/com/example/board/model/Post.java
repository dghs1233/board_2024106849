package com.example.board.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_board") // 사용자가 요청한 테이블 이름
@SQLDelete(sql = "UPDATE tbl_board SET is_del = true WHERE id = ?") // 삭제 요청 시 is_del = true로 업데이트
@Where(clause = "is_del = false") // 조회 시 항상 is_del = false인 것만 조회
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 'tbl_board'의 'user_id' 컬럼과 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자

    @Column(nullable = false, length = 100)
    private String title;

    @Lob // 대용량 텍스트
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ★★★ [오류 수정] updatedAt 필드 추가 ★★★
    @UpdateTimestamp // 엔티티가 수정될 때마다 자동으로 현재 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 'is_del' 컬럼 (Soft Delete)
    @Column(name = "is_del")
    @ColumnDefault("false") // 기본값 false
    private boolean isDel = false;

    // 'view_count' 컬럼 (조회수)
    @Column(name = "view_count")
    @ColumnDefault("0") // 기본값 0
    private int viewCount = 0;

    // 'recommend_count' 컬럼 (추천수)
    // RecommendationService가 이 값을 업데이트합니다.
    @Column(name = "recommend_count")
    @ColumnDefault("0") // 기본값 0
    private int recommendationCount = 0;

    // 'Post'가 삭제되면 연관된 'Comment'도 모두 삭제 (Cascade)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt asc") // 댓글을 생성 시간순으로 정렬
    private List<Comment> comments;

    // 'Post'가 삭제되면 연관된 'Recommendation'도 모두 삭제 (Cascade)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Recommendation> recommendations;
}

