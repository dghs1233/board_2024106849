package com.example.board.service;

import com.example.board.model.Post;
import com.example.board.model.User;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> findAll() {
        // TODO: 추후 Paging 또는 isDel=false 조건 추가
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * ID로 게시글 단일 조회 (조회수 증가 없음)
     * (update, delete 등 서비스 내부 로직에서 사용)
     * @param id 조회할 게시글 ID
     * @return 조회된 Post 객체
     */
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + id));
    }

    /**
     * ID로 게시글 단일 조회 (★조회수 증가 포함★)
     * (Controller에서 사용자에게 게시글을 보여줄 때 사용)
     * @param id 조회할 게시글 ID
     * @return 조회된 Post 객체
     */
    @Transactional // 쓰기 작업(조회수 증가)이 포함되므로 @Transactional 적용
    public Post getPostDetail(Long id) {
        Post post = findById(id); // 1. 내부 로직으로 게시글 조회
        post.setViewCount(post.getViewCount() + 1); // 2. 조회수 증가 (더티 체킹)
        // @Transactional이 종료될 때 변경 감지로 인해 UPDATE 쿼리가 실행됨
        return post; // 3. 게시글 반환
    }


    /**
     * 인기글 5개를 조회합니다. (추천수 5이상, 추천수 내림차순)
     * @return 인기글 5개 목록
     */
    public List<Post> findPopular5() {
        return postRepository.findTop5ByRecommendationCountGreaterThanEqualOrderByRecommendationCountDesc(5);
    }
    /**
     * 특정 사용자가 작성한 게시글 수를 조회합니다.
     * @param userId 사용자 ID
     * @return 작성한 게시글 수
     */
    public long countByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return postRepository.countByUser(user);
    }

    /**
     * 특정 사용자가 받은 총 추천수를 조회합니다.
     * @param userId 사용자 ID
     * @return 받은 총 추천수
     */
    public long getTotalRecommendationsByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return postRepository.sumRecommendationCountByUser(user);
    }
    /**
     * 새 게시글 저장 (userId 기준)
     * @param post 저장할 Post 객체
     * @param userId 현재 로그인한 사용자의 ID
     * @return 저장된 Post 객체
     */
    @Transactional
    public Post save(Post post, String userId) {
        // principal.getName()은 이제 SecurityConfig에서 설정한 "user_id"를 반환합니다.
        User user = userRepository.findByUserId(userId) // <- 수정됨
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId)); // <- 수정됨

        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        // view_count와 recommend_count는 Post 엔티티에서 @ColumnDefault("0")으로 초기화됨

        return postRepository.save(post);
    }

    /**
     * 게시글 수정 (userId 기준)
     * @param id 수정할 게시글 ID
     * @param postDetails 수정할 내용
     * @param userId 현재 로그인한 사용자 ID
     * @return 수정된 Post 객체
     */
    @Transactional
    public Post update(Long id, Post postDetails, String userId) {
        // 조회수 증가가 없는 'findById' 사용
        Post post = findById(id);

        // principal.getName()은 이제 "user_id"입니다.
        if (!post.getUser().getUserId().equals(userId)) { // <- 수정됨 (getUsername() -> getUserId())
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return post; // @Transactional에 의해 더티 체킹
    }

    /**
     * 게시글 삭제 (userId 기준)
     * @param id 삭제할 게시글 ID
     * @param userId 현재 로그인한 사용자 ID
     */
    @Transactional
    public void delete(Long id, String userId) {
        // 조회수 증가가 없는 'findById' 사용
        Post post = findById(id);

        // principal.getName()은 이제 "user_id"입니다.
        if (!post.getUser().getUserId().equals(userId)) { // <- 수정됨 (getUsername() -> getUserId())
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }
    /**
     * 메인 페이지용 최신 게시글 10개를 조회합니다.
     * @return 최신 게시글 10개 목록
     */
    public List<Post> findLatest10() {
        return postRepository.findTop10ByOrderByCreatedAtDesc();
    }
    /**
     * 특정 사용자가 작성한 게시글을 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자가 작성한 게시글 목록
     */
    public List<Post> findPostsByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }
}

