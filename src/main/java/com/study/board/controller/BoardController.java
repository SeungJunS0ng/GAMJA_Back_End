package com.study.board.controller;

import com.study.board.dto.BoardDTO;
import com.study.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/board")
@Slf4j
@Tag(name = "게시판", description = "게시판 관리 API")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    @Operation(summary = "게시글 목록 조회", description = "페이징된 게시글 목록을 조회합니다.")
    public String boardList(Model model,
                           @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "0") int page,
                           @Parameter(description = "검색 키워드") @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                           @Parameter(description = "검색 타입") @RequestParam(value = "searchType", defaultValue = "all") String searchType) {

        log.info("게시글 목록 요청 - 페이지: {}, 검색어: {}, 검색타입: {}", page, searchKeyword, searchType);

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<BoardDTO> paging;

        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            paging = boardService.boardList(pageable);
        } else {
            paging = boardService.boardSearchList(searchKeyword, searchType, pageable);
        }

        model.addAttribute("paging", paging);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("searchType", searchType);

        return "boardlist";
    }

    @GetMapping("/write")
    @Operation(summary = "게시글 작성 폼", description = "새 게시글 작성 폼을 표시합니다.")
    public String boardWriteForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        return "boardwrite";
    }

    @PostMapping("/writepro")
    @Operation(summary = "게시글 작성 처리", description = "새 게시글을 작성합니다.")
    public String boardWritePro(@Valid @ModelAttribute BoardDTO boardDTO,
                               BindingResult bindingResult,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        log.info("게시글 작성 처리 - 제목: {}", boardDTO.getTitle());

        if (bindingResult.hasErrors()) {
            log.warn("게시글 작성 유효성 검증 실패: {}", bindingResult.getAllErrors());
            model.addAttribute("boardDTO", boardDTO);
            return "boardwrite";
        }

        try {
            BoardDTO savedBoard = boardService.write(boardDTO, file);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다.");
            return "redirect:/board/view?id=" + savedBoard.getId();
        } catch (Exception e) {
            log.error("게시글 작성 실패: {}", e.getMessage());
            model.addAttribute("boardDTO", boardDTO);
            model.addAttribute("error", e.getMessage());
            return "boardwrite";
        }
    }

    @GetMapping("/view")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    public String boardView(Model model,
                           @Parameter(description = "게시글 ID") @RequestParam Integer id,
                           RedirectAttributes redirectAttributes) {

        log.info("게시글 상세 조회 - ID: {}", id);

        try {
            BoardDTO boardDTO = boardService.boardView(id);
            model.addAttribute("board", boardDTO);
            return "boardview";
        } catch (Exception e) {
            log.error("게시글 조회 실패 - ID: {}, 오류: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
            return "redirect:/board/list";
        }
    }

    @GetMapping("/delete")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    public String boardDelete(@Parameter(description = "게시글 ID") @RequestParam Integer id,
                             RedirectAttributes redirectAttributes) {

        log.info("게시글 삭제 - ID: {}", id);

        try {
            boardService.boardDelete(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 실패 - ID: {}, 오류: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글 삭제에 실패했습니다.");
        }

        return "redirect:/board/list";
    }

    @GetMapping("/modify/{id}")
    @Operation(summary = "게시글 수정 폼", description = "게시글 수정 폼을 표시합니다.")
    public String boardModify(@Parameter(description = "게시글 ID") @PathVariable("id") Integer id,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        log.info("게시글 수정 폼 - ID: {}", id);

        try {
            BoardDTO boardDTO = boardService.boardViewWithoutIncrement(id);
            model.addAttribute("boardDTO", boardDTO);
            return "boardmodify";
        } catch (Exception e) {
            log.error("게시글 수정 폼 로드 실패 - ID: {}, 오류: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
            return "redirect:/board/list";
        }
    }

    @PostMapping("/update/{id}")
    @Operation(summary = "게시글 수정 처리", description = "게시글을 수정합니다.")
    public String boardUpdate(@Parameter(description = "게시글 ID") @PathVariable("id") Integer id,
                             @Valid @ModelAttribute BoardDTO boardDTO,
                             BindingResult bindingResult,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        log.info("게시글 수정 처리 - ID: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("게시글 수정 유효성 검증 실패: {}", bindingResult.getAllErrors());
            boardDTO.setId(id);
            model.addAttribute("boardDTO", boardDTO);
            return "boardmodify";
        }

        try {
            boardService.updateBoard(id, boardDTO, file);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/board/view?id=" + id;
        } catch (Exception e) {
            log.error("게시글 수정 실패 - ID: {}, 오류: {}", id, e.getMessage());
            boardDTO.setId(id);
            model.addAttribute("boardDTO", boardDTO);
            model.addAttribute("error", e.getMessage());
            return "boardmodify";
        }
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 목록", description = "조회수 기준 인기 게시글 목록을 조회합니다.")
    public String popularPosts(Model model,
                              @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "0") int page) {

        log.info("인기 게시글 목록 요청 - 페이지: {}", page);

        Pageable pageable = PageRequest.of(page, 10);
        Page<BoardDTO> paging = boardService.getPopularPosts(pageable);

        model.addAttribute("paging", paging);
        model.addAttribute("isPopular", true);

        return "boardlist";
    }

    @GetMapping("/download")
    @Operation(summary = "파일 다운로드", description = "게시글의 첨부파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadFile(@Parameter(description = "게시글 ID") @RequestParam Integer id) {

        log.info("파일 다운로드 요청 - 게시글 ID: {}", id);

        try {
            BoardDTO boardDTO = boardService.boardViewWithoutIncrement(id);

            if (boardDTO.getFilepath() == null) {
                throw new RuntimeException("첨부파일이 없습니다.");
            }

            Path filePath = Paths.get(boardDTO.getFilepath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + boardDTO.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("파일 다운로드 실패 - 게시글 ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
