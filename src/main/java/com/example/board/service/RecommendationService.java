package com.example.board.service;

import com.example.board.model.Post;
import com.example.board.model.Recommendation;
import com.example.board.model.User;
import com.example.board.repository.PostRepository;
import com.example.board.repository.RecommendationRepository;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleRecommendation(Long postId, String userId) {
        // 1. 사용자 및 게시글 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

        // 2. 기존 추천 기록 조회 (★ 메서드명/파라미터 순서 수정 ★)
        Optional<Recommendation> existingRec = recommendationRepository.findByUserAndPost(user, post);

        if (existingRec.isPresent()) {
            // 3a. 추천 취소 (기록 삭제, 카운트 -1)
            recommendationRepository.delete(existingRec.get());
            post.setRecommendationCount(post.getRecommendationCount() - 1);
            return false; // 추천 취소됨
        } else {
            // 3b. 추천 (기록 생성, 카운트 +1)
            Recommendation newRec = new Recommendation(); // User, Post는 setter로 설정
            newRec.setUser(user);
            newRec.setPost(post);
            newRec.setCreatedAt(LocalDateTime.now());
            recommendationRepository.save(newRec);
            post.setRecommendationCount(post.getRecommendationCount() + 1);
            return true; // 추천됨
        }
    }

    // ★★★ [오류 수정] isRecommended 메서드 추가 ★★★
    /**
     * 현재 사용자가 해당 게시글을 추천했는지 확인합니다. (detail.html 표시용)
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 추천했다면 true, 아니면 false
     */
    public boolean isRecommended(Long postId, String userId) {
        // 1. 사용자 조회 (존재하지 않으면 false)
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        // 2. 게시글 조회 (존재하지 않으면 false)
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return false;
        }

        // 3. 추천 기록 조회 (★ 메서드명/파라미터 순서 수정 ★)
        Optional<Recommendation> existingRec = recommendationRepository.findByUserAndPost(userOpt.get(), postOpt.get());

        // 4. 기록이 있으면(isPresent) true, 없으면 false 반환
        return existingRec.isPresent();
    }
}

