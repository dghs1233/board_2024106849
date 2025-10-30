package com.example.board.service;

import com.example.board.model.Comment;
import com.example.board.model.Post;
import com.example.board.model.User;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 댓글(Comment) 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository; // userId로 변경된 것을 사용

    /**
     * 특정 게시글의 모든 댓글을 조회합니다.
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    public List<Comment> findCommentsByPostId(Long postId) {
        // @Where 어노테이션 덕분에 is_del = false인 댓글만 조회됨
        return commentRepository.findByPostId(postId);
    }

    /**
     * 새 댓글을 작성합니다. (★익명 ID 할당 로직 수정★)
     * @param postId 댓글을 작성할 게시글 ID
     * @param content 댓글 내용
     * @param userId 작성자 ID
     * @return 저장된 댓글
     */
    @Transactional // 쓰기 작업이므로 트랜잭션 적용
    public Comment createComment(Long postId, String content, String userId) {
        // 1. 사용자 조회 (userId 사용)
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

        // 3. (★핵심 로직★) 익명 ID 할당 (long -> int로 수정)
        Integer anonymousId; // ★ long -> Integer로 수정
        // 3a. 게시글 작성자와 댓글 작성자가 동일한지 확인
        if (post.getUser().getId().equals(user.getId())) {
            // 작성자 본인이면 anonymousId = 0
            anonymousId = 0; // ★ 0L -> 0으로 수정
        } else {
            // 3b. 작성자가 아니면, 이 게시글에 '익명으로' 댓글을 단 사용자인지 확인
            // (수정) 0보다 큰 ID(익명)만 찾도록 새 리포지토리 메서드 사용
            Optional<Comment> existingComment = commentRepository
                    .findFirstByPostAndUserAndAnonymousIdGreaterThanOrderByCreatedAtAsc(post, user, 0);

            if (existingComment.isPresent()) {
                // 이미 익명 댓글을 단 사용자면, 기존 anonymousId 사용
                anonymousId = existingComment.get().getAnonymousId();
            } else {
                // 3c. 이 게시글에 처음 댓글을 다는 익명 사용자
                // 현재 게시글의 최대 익명 ID를 조회 (0(작성자) 제외)
                // (수정) 리포지토리 메서드가 Integer를 반환
                Integer maxId = commentRepository.findMaxAnonymousIdByPost(post).orElse(0); // ★ long -> Integer로 수정
                anonymousId = maxId + 1; // '익명1', '익명2'...
            }
        }

        // 4. 댓글 엔티티 생성
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);
        comment.setAnonymousId(anonymousId); // ★ 할당된 익명 ID 설정

        // 5. 저장
        return commentRepository.save(comment);
    }

    /**
     * 댓글을 삭제합니다.
     * @param commentId 삭제할 댓글 ID
     * @param userId 삭제를 요청한 사용자 ID
     */
    @Transactional // 쓰기 작업이므로 트랜잭션 적용
    public void deleteComment(Long commentId, String userId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));

        // 2. 사용자 조회 (userId 사용)
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 3. 권한 확인: 댓글 작성자와 현재 로그인한 사용자가 동일한지 확인
        // (수정) user.getUsername() -> user.getUserId()
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }

        // 4. 삭제 (엔티티의 @SQLDelete가 논리적 삭제로 처리함)
        commentRepository.delete(comment);
    }
}

