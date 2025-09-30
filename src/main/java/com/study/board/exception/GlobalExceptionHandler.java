package com.study.board.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;

/**
 * 전역 예외 처리 핸들러
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 엔티티를 찾을 수 없을 때 발생하는 예외 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute("message", "요청하신 게시물을 찾을 수 없습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    /**
     * 잘못된 인수가 전달되었을 때 발생하는 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        model.addAttribute("message", "잘못된 요청입니다: " + e.getMessage());
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    /**
     * 파일을 찾을 수 없을 때 발생하는 예외 처리
     */
    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFound(FileNotFoundException e, Model model) {
        model.addAttribute("message", "요청하신 파일을 찾을 수 없습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    /**
     * 파일 업로드 크기 초과 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e, Model model) {
        model.addAttribute("message", "업로드 파일 크기가 너무 큽니다. 10MB 이하의 파일을 업로드해주세요.");
        model.addAttribute("searchUrl", "/board/write");
        return "message";
    }

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception e, Model model) {
        model.addAttribute("message", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }
}
