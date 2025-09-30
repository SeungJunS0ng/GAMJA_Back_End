package com.study.board.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 게시판 엔티티 클래스.
 */
@Entity
@Data
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    @Column(name = "view_count", nullable = false, columnDefinition = "integer default 0")
    private Integer viewCount = 0;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "file_path")
    private String filepath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 조회수 증가 메서드
     */
    public void increaseViewCount() {
        this.viewCount = (this.viewCount == null) ? 1 : this.viewCount + 1;
    }

    /**
     * 엔티티 생성 전 처리
     */
    @PrePersist
    public void prePersist() {
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
    }
}