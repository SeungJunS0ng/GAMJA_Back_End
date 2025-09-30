package com.study.board.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 게시물 데이터 전송 객체 (DTO).
 */
@Data
public class BoardDTO {

    private Integer id;           // 게시물 ID

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
    private String title;         // 게시물 제목

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Size(max = 4000, message = "내용은 4000자 이하로 입력해주세요.")
    private String content;       // 게시물 내용

    private String filename;      // 첨부된 파일 이름
    private String filepath;      // 첨부된 파일 경로
    private LocalDateTime createdAt;  // 게시물 생성일시
    private LocalDateTime updatedAt;  // 게시물 수정일시
}