package com.example.board.repository;

import com.example.board.model.Post;
import com.example.board.model.Recommendation;
import com.example.board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 게시글 추천(좋아요) 데이터를 처리하는 JpaRepository
 */
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    /**
     * [핵심] 특정 사용자가 특정 게시글을 이미 추천했는지 확인 (중복 추천 방지용)
     *
     * @param user 확인할 사용자
     * @param post 확인할 게시글
     * @return 추천했다면 true, 아니면 false
     */
    boolean existsByUserAndPost(User user, Post post);

    /**
     * [핵심] 특정 사용자가 특정 게시글에 누른 추천 기록을 찾기 (추천 취소 시 필요)
     *
     * @param user 찾을 사용자
     * @param post 찾을 게시글
     * @return Recommendation 엔티티 (Optional)
     */
    Optional<Recommendation> findByUserAndPost(User user, Post post);

    /**
     * [핵심] 특정 게시글의 총 추천 수를 계산
     *
     * @param post 추천 수를 계산할 게시글
     * @return 총 추천 수 (long)
     */
    long countByPost(Post post);

    // 참고: ID로도 카운트할 수 있습니다.
    // long countByPostId(Long postId);
}
