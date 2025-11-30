package com.example.board.controller;

import com.example.board.model.User;
import com.example.board.service.CommentService;
import com.example.board.service.PostService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * 마이페이지 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    /**
     * 마이페이지 메인 (GET /user/mypage)
     */
    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal) {
        String userId = principal.getName();

        try {
            // 사용자 정보 조회
            User user = userService.findByUserId(userId);
            model.addAttribute("user", user);

            // 사용자 통계 정보
            long postCount = postService.countByUserId(userId);
            long commentCount = commentService.countByUserId(userId);
            long totalRecommendations = postService.getTotalRecommendationsByUserId(userId);

            model.addAttribute("userPostCount", postCount);
            model.addAttribute("userCommentCount", commentCount);
            model.addAttribute("userTotalRecommendations", totalRecommendations);

            // 내가 쓴 게시글 목록 (최신 10개)
            model.addAttribute("myPosts", postService.findPostsByUserId(userId));

            // 내가 쓴 댓글 목록 (최신 10개)
            model.addAttribute("myComments", commentService.findCommentsByUserId(userId));

        } catch (Exception e) {
            // 오류 발생 시 기본값 설정
            model.addAttribute("userPostCount", 0L);
            model.addAttribute("userCommentCount", 0L);
            model.addAttribute("userTotalRecommendations", 0L);
        }

        return "user/mypage"; // templates/user/mypage.html
    }

    /**
     * 비밀번호 변경 폼 (GET /user/change-password)
     */
    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "user/change-password"; // templates/user/change-password.html
    }

    /**
     * 비밀번호 변경 처리 (POST /user/change-password)
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        String userId = principal.getName();

        // 새 비밀번호 확인
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/user/change-password";
        }

        try {
            userService.changePassword(userId, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
            return "redirect:/user/mypage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/change-password";
        }
    }
}