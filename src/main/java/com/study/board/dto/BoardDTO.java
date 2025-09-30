package com.study.board.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    private Integer id;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Size(max = 4000, message = "내용은 4000자 이하로 입력해주세요.")
    private String content;

    @NotBlank(message = "작성자는 필수 입력 항목입니다.")
    @Size(max = 50, message = "작성자는 50자 이하로 입력해주세요.")
    private String author;

    private Integer viewCount;

    private String filename;

    private String filepath;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
