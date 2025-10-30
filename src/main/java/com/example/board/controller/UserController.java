package com.example.board.controller;

import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor // final 필드(UserService)에 대한 생성자 자동 주입
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 폼을 보여주는 GET 요청을 처리합니다.
     * @return 회원가입 폼 템플릿 (register.html)
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        // "user/register" 템플릿을 반환
        return "user/register";
    }

    /**
     * 회원가입 폼에서 POST 요청을 처리합니다.
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param model 뷰에 전달할 모델
     * @return 성공 시 로그인 페이지로 리다이렉트, 실패 시 회원가입 폼으로 복귀
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam("user_id") String userId,
            @RequestParam("password") String password,
            Model model) {

        try {
            userService.register(userId, password);
            // 회원가입 성공 시 로그인 페이지로 리다이렉트 (성공 파라미터 추가)
            return "redirect:/user/login?success=true";
        } catch (RuntimeException e) { // <- IllegalArgumentException과 IllegalStateException 모두 잡도록 수정
            // 사용자 ID 중복 등 예외 발생 시
            model.addAttribute("error", e.getMessage());
            // 리다이렉트 없이 바로 회원가입 폼을 재렌더링
            return "user/register";
        }
    }

    /**
     * 로그인 폼을 보여주는 GET 요청을 처리합니다.
     * @return 로그인 폼 템플릿 (login.html)
     */
    @GetMapping("/login")
    public String showLoginForm() {
        // "user/login" 템플릿을 반환
        return "user/login";
    }

    /**
     * 로그인 실패 시 처리합니다. (SecurityConfig에서 설정)
     * @param model 뷰에 전달할 모델
     * @return 로그인 폼 템플릿 (login.html)
     */
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return "user/login";
    }
}

