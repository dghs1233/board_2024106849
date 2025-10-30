package com.example.board.repository;

import com.example.board.model.Comment;
import com.example.board.model.Post;
import com.example.board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 특정 게시글(Post ID)에 해당하는 댓글 목록을 조회합니다.
     * (is_del = false 등을 추가로 고려할 수 있습니다.)
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    List<Comment> findByPostId(Long postId);

    /**
     * (★익명 기능★)
     * 특정 게시글(Post)에서 특정 사용자(User)가 작성한 첫 번째 댓글을 찾습니다.
     * (이 사용자의 기존 'anonymous_id'를 확인하기 위함)
     * @param post 게시글 엔티티
     * @param user 사용자 엔티티
     * @return 댓글 (Optional)
     */
    Optional<Comment> findTopByPostAndUserOrderByCreatedAtAsc(Post post, User user);
    Optional<Comment> findFirstByPostAndUserAndAnonymousIdGreaterThanOrderByCreatedAtAsc(Post post, User user, int anonymousId);
    /**
     * (★익명 기능★)
     * 특정 게시글(Post)에서 0(작성자)을 제외한 가장 큰 'anonymous_id'를 찾습니다.
     * (새로운 익명 사용자에게 다음 ID를 부여하기 위함)
     * @param post 게시글 엔티티
     * @return 가장 큰 anonymous_id (Optional)
     */
    @Query("SELECT MAX(c.anonymousId) FROM Comment c WHERE c.post = :post AND c.anonymousId > 0")
    Optional<Integer> findMaxAnonymousIdByPost(@Param("post") Post post);
}

