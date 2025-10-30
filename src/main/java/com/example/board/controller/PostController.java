package com.example.board.controller;

import com.example.board.model.Post;
import com.example.board.service.CommentService;
import com.example.board.service.PostService;
import com.example.board.service.RecommendationService; // RecommendationService import
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * 게시글(Post) 관련 웹 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/posts") // /posts로 시작하는 URL 요청을 처리
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class PostController {

    private final PostService postService;
    private final CommentService commentService; // 댓글 조회를 위해 CommentService 주입
    private final RecommendationService recommendationService; // 추천 기능 C-Service 주입

    /**
     * 게시글 목록 페이지 (GET /posts)
     */
    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "posts/list"; // templates/posts/list.html
    }

    /**
     * 게시글 상세 보기 페이지 (GET /posts/{id})
     * (★조회수 증가 로직 반영★)
     */
    @GetMapping("/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        // [수정됨] findById -> getPostDetail (조회수 증가 O)
        Post post = postService.getPostDetail(id);
        model.addAttribute("post", post);

        // 댓글 목록 추가
        model.addAttribute("comments", commentService.findCommentsByPostId(id));

        // [추가됨] 현재 사용자의 추천 여부 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isRecommended = false;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String userId = authentication.getName();
            isRecommended = recommendationService.isRecommended(id, userId);
        }
        model.addAttribute("isRecommended", isRecommended);

        return "posts/detail"; // templates/posts/detail.html
    }

    /**
     * 새 게시글 작성 폼 페이지 (GET /posts/new)
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("post", new Post()); // 비어 있는 Post 객체를 모델에 추가
        return "posts/form"; // templates/posts/form.html
    }

    /**
     * 새 게시글 저장 (POST /posts) (★매핑이 "/new"에서 ""로 수정됨★)
     */
    @PostMapping
    public String createPost(Post post, Principal principal) {
        String userId = principal.getName(); // 현재 로그인한 사용자의 ID (userId)
        Post savedPost = postService.save(post, userId);
        return "redirect:/posts/" + savedPost.getId(); // 저장 후 상세 페이지로 리다이렉트
    }

    /**
     * 게시글 수정 폼 페이지 (GET /posts/edit/{id})
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.findById(id)); // (조회수 증가 X)
        return "posts/edit-form"; // templates/posts/edit-form.html
    }

    /**
     * 게시글 수정 (POST /posts/edit/{id})
     */
    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, Post post, Principal principal) {
        String userId = principal.getName();
        postService.update(id, post, userId);
        return "redirect:/posts/" + id; // 수정 후 상세 페이지로 리다이렉트
    }

    /**
     * 게시글 삭제 (POST /posts/delete/{id})
     */
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        String userId = principal.getName();
        postService.delete(id, userId);
        return "redirect:/posts"; // 삭제 후 목록 페이지로 리다이렉트
    }

    /**
     * 게시글 추천 토글 (POST /posts/{id}/recommend)
     * (★추가된 메서드★)
     */
    @PostMapping("/{id}/recommend")
    public String toggleRecommend(@PathVariable Long id, Principal principal) {
        String userId = principal.getName(); // 현재 로그인한 사용자 ID
        recommendationService.toggleRecommendation(id, userId);
        return "redirect:/posts/" + id; // 추천/취소 후 상세 페이지로 리다이렉트
    }
}