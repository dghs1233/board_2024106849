# 익명 웹 게시판 프로젝트

## 프로젝트 개요
- **과목**: 컴퓨터 프로젝트
- **주제**: 익명 웹 게시판
- **개발 환경**: Java 17, Spring Boot, Spring Data JPA, Thymeleaf

## 주요 기능
1. **사용자 관리**
    - 회원가입/로그인/로그아웃
    - 마이페이지 (작성글/댓글 조회)
    - 비밀번호 변경

2. **게시판 기능**
    - 게시글 작성/수정/삭제
    - 게시글 목록 조회
    - 게시글 상세 보기

3. **댓글 시스템**
    - 익명 댓글 기능 (같은 게시글 내에서 동일 사용자는 같은 익명 ID / 작성자는 "작성자")
    - 댓글 작성/삭제

4. **추천 시스템**
    - 게시글 추천/비추천 기능

## 기술 스택
- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, HTML, CSS
- **Database**: MySQL
- **Build Tool**: Gradle

## 실행 방법
1. 프로젝트 클론 후 IDE에서 열기
2. BoardApplication 실행
3. 브라우저에서 `http://localhost:8080` 접속

## 주요 특징
- **익명성 보장**: 댓글에서 사용자별 고유 익명 ID 부여
- **보안**: Spring Security를 통한 인증/인가
