package com.example.board.controller;

import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 댓글(Comment) 관련 웹 요청을 처리하는 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/comments") // /comments로 시작하는 URL을 처리
public class CommentController {

    private final CommentService commentService;

    /**
     * 새 댓글을 생성합니다.
     * 이 메서드는 /posts/{postId}/comments URL로 POST 요청을 받도록 PostController에 만들 수도 있지만,
     * 여기서는 /comments/create/{postId}로 분리했습니다.
     *
     * @param postId 댓글이 달릴 게시글 ID
     * @param content 댓글 내용
     * @param userDetails 현재 로그인한 사용자 정보 (Spring Security가 제공)
     * @param redirectAttributes 리다이렉트 시 메시지를 전달하기 위함
     * @return 게시글 상세 페이지로 리다이렉트
     */
    @PostMapping("/create/{postId}")
    public String createComment(@PathVariable("postId") Long postId,
                                @RequestParam("content") String content,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            // 로그인하지 않은 사용자는 로그인 페이지로 리다이렉트 (SecurityConfig에서 처리되지만 이중 방어)
            return "redirect:/user/login";
        }

        try {
            commentService.createComment(postId, content, userDetails.getUsername());
        } catch (IllegalArgumentException e) {
            // 사용자를 찾을 수 없거나 게시글을 찾을 수 없는 경우
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // 댓글 작성 후 다시 해당 게시글 상세 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }

    /**
     * 댓글을 삭제합니다.
     * @param commentId 삭제할 댓글 ID
     * @param postId 댓글이 속한 게시글 ID (리다이렉트용)
     * @param userDetails 현재 로그인한 사용자 정보
     * @param redirectAttributes 리다이렉트 시 메시지를 전달하기 위함
     * @return 게시글 상세 페이지로 리다이렉트
     */
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @RequestParam("postId") Long postId, // [수정] 리다이렉트를 위해 postId를 받습니다.
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/user/login";
        }

        try {
            commentService.deleteComment(commentId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 삭제되었습니다.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글을 삭제할 권한이 없습니다.");
        }

        // [수정] 삭제 성공/실패 여부와 관계없이, 작업이 끝나면 원래의 게시글 상세 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }
}

