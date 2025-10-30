package com.example.board.repository;

import com.example.board.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Post 엔티티에 대한 데이터베이스 접근을 처리하는 JpaRepository입니다.
 * JpaRepository<Post, Long>를 상속받음으로써 기본적인 CRUD(Create, Read, Update, Delete)
 * 메서드(예: save, findById, findAll, deleteById)를 자동으로 사용할 수 있습니다.
 */
@Repository // Spring Data JPA 리포지토리임을 나타냅니다.
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 생성 날짜(createdAt)를 기준으로 내림차순 정렬하여
     * 상위 5개의 게시글을 조회합니다. (메인 페이지용)
     * (Finds the top 5 posts, ordered by createdAt descending (for main page))
     * @return 최신 게시글 5개 목록 (List of 5 latest posts)
     */
    List<Post> findTop5ByOrderByCreatedAtDesc();

    List<Post> findAllByOrderByCreatedAtDesc();
}
