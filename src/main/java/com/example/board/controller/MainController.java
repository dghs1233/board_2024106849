package com.example.board.controller;

import com.example.board.service.CommentService;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 페이지("/") 요청을 처리하는 컨트롤러
 */
@Controller
@RequiredArgsConstructor // PostService 주입을 위한 생성자 자동 생성
public class MainController {

    private final PostService postService; // 최신 글 조회를 위해 PostService 주입
    private final CommentService commentService; // 댓글 통계를 위해 CommentService 주입

    /**
     * 메인 페이지 (GET /)
     * @param model 뷰에 데이터를 전달할 모델
     * @return 메인 페이지 템플릿 (index.html)
     */
    @GetMapping("/")
    public String home(Model model) {

        // [수정됨] 최신 게시글 10개를 조회하여 모델에 추가
        model.addAttribute("latestPosts", postService.findLatest10());
        model.addAttribute("popularPosts", postService.findPopular5());

        // [추가됨] 로그인한 사용자의 통계 정보 추가
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String userId = authentication.getName();
            try {
                long postCount = postService.countByUserId(userId);
                long commentCount = commentService.countByUserId(userId);
                long totalRecommendations = postService.getTotalRecommendationsByUserId(userId);

                model.addAttribute("userPostCount", postCount);
                model.addAttribute("userCommentCount", commentCount);
                model.addAttribute("userTotalRecommendations", totalRecommendations);
            } catch (Exception e) {
                // 통계 조회 실패 시 기본값 설정
                model.addAttribute("userPostCount", 0L);
                model.addAttribute("userCommentCount", 0L);
                model.addAttribute("userTotalRecommendations", 0L);
            }
        }

        return "index"; // templates/index.html
    }
}