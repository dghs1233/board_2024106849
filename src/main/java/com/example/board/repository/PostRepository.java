package com.example.board.repository;

import com.example.board.model.Post;
import com.example.board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * 생성 날짜(createdAt)를 기준으로 내림차순 정렬하여
     * 상위 10개의 게시글을 조회합니다. (메인 페이지용)
     * @return 최신 게시글 10개 목록
     */
    List<Post> findTop10ByOrderByCreatedAtDesc();

    List<Post> findAllByOrderByCreatedAtDesc();

    /**
     * 추천수가 지정된 값 이상인 게시글을 추천수 내림차순으로 상위 5개 조회합니다.
     * (인기글 목록용)
     * @param minRecommendationCount 최소 추천수
     * @return 인기글 5개 목록
     */
    List<Post> findTop5ByRecommendationCountGreaterThanEqualOrderByRecommendationCountDesc(Integer minRecommendationCount);

    /**
     * 특정 사용자가 작성한 게시글 수를 조회합니다.
     * @param user 사용자
     * @return 작성한 게시글 수
     */
    long countByUser(User user);

    /**
     * 특정 사용자가 작성한 게시글의 총 추천수를 조회합니다.
     * @param user 사용자
     * @return 총 추천수
     */
    @Query("SELECT COALESCE(SUM(p.recommendationCount), 0) FROM Post p WHERE p.user = :user")
    Long sumRecommendationCountByUser(@Param("user") User user);

    /**
     * 특정 사용자가 작성한 게시글을 최신순으로 조회합니다.
     * @param user 사용자
     * @return 사용자가 작성한 게시글 목록
     */
    List<Post> findByUserOrderByCreatedAtDesc(User user);



}