package com.example.board.controller;

import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
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

    /**
     * 메인 페이지 (GET /)
     * @param model 뷰에 데이터를 전달할 모델
     * @return 메인 페이지 템플릿 (index.html)
     */
    @GetMapping("/")
    public String home(Model model) {

        // [추가됨] 최신 게시글 5개를 조회하여 모델에 추가
        model.addAttribute("latestPosts", postService.findLatest5());

        return "index"; // templates/index.html
    }
}

