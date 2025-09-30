package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 제목으로 검색
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);

    // 제목 또는 내용으로 검색
    Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 작성자로 검색
    Page<Board> findByAuthorContaining(String author, Pageable pageable);

    // 조회수 증가
    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void increaseViewCount(@Param("id") Integer id);

    // 인기 게시물 조회 (조회수 기준)
    @Query("SELECT b FROM Board b ORDER BY b.viewCount DESC, b.createdAt DESC")
    Page<Board> findPopularPosts(Pageable pageable);
}
