package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시판 엔티티에 대한 데이터베이스 접근을 제공하는 레포지토리 인터페이스.
 *
 * JpaRepository를 확장하여 기본적인 CRUD 및 페이징 기능을 제공합니다.
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    /**
     * 제목에 검색 키워드가 포함된 게시물을 페이징하여 조회합니다.
     *
     * @param searchKeyword 검색할 제목의 키워드
     * @param pageable 페이징 정보
     * @return 제목에 검색 키워드가 포함된 게시물 목록 (페이징 처리됨)
     */
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);

    /**
     * 제목 또는 내용에 검색 키워드가 포함된 게시물을 페이징하여 조회합니다.
     *
     * @param titleKeyword 제목 검색 키워드
     * @param contentKeyword 내용 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 게시물 목록 (페이징 처리됨)
     */
    Page<Board> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

    /**
     * 작성자로 게시물을 검색합니다.
     *
     * @param author 작성자명
     * @param pageable 페이징 정보
     * @return 작성자의 게시물 목록 (페이징 처리됨)
     */
    Page<Board> findByAuthorContaining(String author, Pageable pageable);

    /**
     * 조회수를 증가시킵니다.
     *
     * @param id 게시물 ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void increaseViewCount(@Param("id") Integer id);

    /**
     * 인기 게시물 조회 (조회수 기준 상위 10개)
     *
     * @param pageable 페이징 정보
     * @return 조회수 기준 상위 게시물 목록
     */
    @Query("SELECT b FROM Board b ORDER BY b.viewCount DESC, b.createdAt DESC")
    Page<Board> findPopularPosts(Pageable pageable);
}