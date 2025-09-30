package com.study.board.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * 게시판 엔티티 클래스.
 *
 * JPA 엔티티로 사용되며, 데이터베이스와 매핑됩니다.
 * Lombok의 @Data 어노테이션으로 getter, setter, toString, equals, hashCode 메소드를 자동 생성합니다.
 */
@Entity
@Data
public class Board {

    /**
     * 게시물의 고유 식별자.
     * 엔티티의 기본키이며, 데이터베이스에서 자동 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 게시물의 제목.
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 게시물의 내용.
     */
    @Column(nullable = false, length = 4000)
    private String content;

    /**
     * 첨부된 파일의 이름.
     */
    private String filename;

    /**
     * 첨부된 파일의 경로.
     */
    private String filepath;

    /**
     * 게시물 생성 일시.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 게시물 수정 일시.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}