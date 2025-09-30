package com.study.board.controller;

import com.study.board.dto.BoardDTO;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + File.separator +
            "src" + File.separator + "main" + File.separator + "resources" + File.separator +
            "static" + File.separator + "files";

    /**
     * 메인 페이지 리다이렉트
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/board/list";
    }

    /**
     * 게시물 작성 폼을 보여주는 메서드
     */
    @GetMapping("/board/write")
    public String boardWriteForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        return "boardwrite";
    }

    /**
     * 게시물을 실제로 작성하는 메서드
     */
    @PostMapping("/board/writepro")
    public String boardWritePro(@Valid @ModelAttribute BoardDTO boardDTO,
                               BindingResult bindingResult,
                               Model model,
                               @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {

        if (bindingResult.hasErrors()) {
            return "boardwrite";
        }

        try {
            boardService.write(boardDTO, file);
            return addMessage(model, "글 작성이 완료되었습니다.", "/board/list");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "boardwrite";
        }
    }

    /**
     * 게시물 목록을 보여주는 메서드 (통합 검색 지원)
     */
    @GetMapping("/board/list")
    public String boardList(Model model,
                           @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                           @RequestParam(required = false) String searchKeyword,
                           @RequestParam(required = false, defaultValue = "all") String searchType) {

        Page<BoardDTO> list;

        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword.trim(), searchType, pageable);
        }

        addPaginationAttributes(model, list);
        model.addAttribute("list", list);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("searchType", searchType);

        if (searchKeyword != null && !searchKeyword.trim().isEmpty() && list.getContent().isEmpty()) {
            model.addAttribute("message", "검색 결과가 없습니다.");
        }

        return "boardlist";
    }

    /**
     * 인기 게시물 목록을 보여주는 메서드
     */
    @GetMapping("/board/popular")
    public String popularBoardList(Model model,
                                  @PageableDefault(page = 0, size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BoardDTO> list = boardService.getPopularPosts(pageable);

        addPaginationAttributes(model, list);
        model.addAttribute("list", list);
        model.addAttribute("isPopularPage", true);

        return "boardlist";
    }

    /**
     * 특정 게시물의 상세 정보를 보여주는 메서드
     */
    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam Integer id) {
        try {
            BoardDTO boardDTO = boardService.boardView(id);
            model.addAttribute("board", boardDTO);
            return "boardview";
        } catch (Exception e) {
            return addMessage(model, "게시물을 찾을 수 없습니다.", "/board/list");
        }
    }

    /**
     * 게시물을 삭제하는 메서드
     */
    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam Integer id, Model model) {
        try {
            boardService.boardDelete(id);
            return addMessage(model, "게시물이 삭제되었습니다.", "/board/list");
        } catch (Exception e) {
            return addMessage(model, "게시물 삭제 중 오류가 발생했습니다.", "/board/list");
        }
    }

    /**
     * 게시물 수정 폼을 보여주는 메서드
     */
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable Integer id, Model model) {
        try {
            BoardDTO boardDTO = boardService.boardViewWithoutIncrement(id);
            model.addAttribute("board", boardDTO);
            model.addAttribute("currentFile", boardDTO.getFilename());
            return "boardmodify";
        } catch (Exception e) {
            return addMessage(model, "게시물을 찾을 수 없습니다.", "/board/list");
        }
    }

    /**
     * 게시물을 수정하는 메서드
     */
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable Integer id,
                             @Valid @ModelAttribute BoardDTO boardDTO,
                             BindingResult bindingResult,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("board", boardDTO);
            return "boardmodify";
        }

        try {
            boardService.updateBoard(id, boardDTO, file);
            return addMessage(model, "게시물이 수정되었습니다.", "/board/view?id=" + id);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("board", boardDTO);
            return "boardmodify";
        }
    }

    /**
     * 파일 다운로드를 처리하는 메서드
     */
    @GetMapping("/board/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer id) throws IOException {
        try {
            BoardDTO boardDTO = boardService.boardViewWithoutIncrement(id);

            if (boardDTO.getFilename() == null || boardDTO.getFilename().isEmpty()) {
                throw new FileNotFoundException("첨부파일이 없습니다.");
            }

            File file = new File(FILE_DIRECTORY, boardDTO.getFilename());

            if (!file.exists()) {
                throw new FileNotFoundException("파일을 찾을 수 없습니다: " + file.getAbsolutePath());
            }

            if (file.length() > 50 * 1024 * 1024) {
                throw new IllegalStateException("파일 크기가 너무 큽니다.");
            }

            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // 원본 파일명 추출 (UUID 제거)
            String originalFilename = boardDTO.getFilename();
            if (originalFilename.contains("_")) {
                originalFilename = originalFilename.substring(originalFilename.indexOf("_") + 1);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                           "attachment; filename=\"" + originalFilename + "\"")
                    .body(fileBytes);

        } catch (Exception e) {
            throw new FileNotFoundException("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * REST API: 게시물 목록 조회
     */
    @GetMapping("/api/boards")
    @ResponseBody
    public ResponseEntity<Page<BoardDTO>> getBoardList(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "all") String searchType) {

        Page<BoardDTO> list;
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword.trim(), searchType, pageable);
        }

        return ResponseEntity.ok(list);
    }

    /**
     * REST API: 게시물 상세 조회
     */
    @GetMapping("/api/boards/{id}")
    @ResponseBody
    public ResponseEntity<BoardDTO> getBoardDetail(@PathVariable Integer id) {
        try {
            BoardDTO boardDTO = boardService.boardView(id);
            return ResponseEntity.ok(boardDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * REST API: 인기 게시물 조회
     */
    @GetMapping("/api/boards/popular")
    @ResponseBody
    public ResponseEntity<Page<BoardDTO>> getPopularBoards(
            @PageableDefault(page = 0, size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BoardDTO> list = boardService.getPopularPosts(pageable);
        return ResponseEntity.ok(list);
    }

    /**
     * 메시지를 추가하는 메서드 (에러 및 성공 메시지 통합)
     */
    private String addMessage(Model model, String message, String searchUrl) {
        model.addAttribute("message", message);
        model.addAttribute("searchUrl", searchUrl);
        return "message";
    }

    /**
     * 페이지 정보를 계산하여 모델에 추가하는 메서드
     */
    private void addPaginationAttributes(Model model, Page<BoardDTO> list) {
        int nowPage = list.getPageable().getPageNumber() + 1;
        int totalPages = list.getTotalPages();
        int pageSize = 10;

        int startPage = Math.max(nowPage - (pageSize / 2), 1);
        int endPage = Math.min(startPage + pageSize - 1, totalPages);

        if (endPage - startPage < pageSize - 1) {
            startPage = Math.max(endPage - (pageSize - 1), 1);
        }

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrevious", list.hasPrevious());
        model.addAttribute("hasNext", list.hasNext());
    }
}