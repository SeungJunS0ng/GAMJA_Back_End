package com.study.board.service;

import com.study.board.dto.BoardDTO;
import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 파일 저장 경로 설정
    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + File.separator +
            "src" + File.separator + "main" + File.separator + "resources" + File.separator +
            "static" + File.separator + "files";

    // 허용되는 파일 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".pdf", ".doc", ".docx", ".txt", ".zip", ".rar"
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 게시물 조회를 위한 메서드 (조회수 증가 포함)
     *
     * @param id 게시물 ID
     * @return 게시물 DTO
     */
    public BoardDTO boardView(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다. ID: " + id));

        // 조회수 증가
        boardRepository.increaseViewCount(id);

        return convertToDTO(board);
    }

    /**
     * 게시물 조회 (조회수 증가 없음)
     *
     * @param id 게시물 ID
     * @return 게시물 DTO
     */
    public BoardDTO boardViewWithoutIncrement(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다. ID: " + id));

        return convertToDTO(board);
    }

    /**
     * 게시물 작성 메서드
     *
     * @param boardDTO 게시물 데이터 전송 객체
     * @param file 첨부 파일
     * @throws Exception 파일 저장 중 오류 발생 시
     */
    public void write(BoardDTO boardDTO, MultipartFile file) throws Exception {
        validateBoardData(boardDTO);

        Board board = new Board();
        board.setTitle(boardDTO.getTitle().trim());
        board.setContent(boardDTO.getContent().trim());
        board.setAuthor(boardDTO.getAuthor().trim());
        board.setViewCount(0);

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            String fileName = saveFile(file);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        boardRepository.save(board);
    }

    /**
     * 게시물 수정 메서드
     *
     * @param id 게시물 ID
     * @param boardDTO 수정된 게시물 데이터 전송 객체
     * @param file 첨부 파일
     * @throws Exception 파일 저장 중 오류 발생 시
     */
    public void updateBoard(Integer id, BoardDTO boardDTO, MultipartFile file) throws Exception {
        validateBoardData(boardDTO);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다. ID: " + id));

        board.setTitle(boardDTO.getTitle().trim());
        board.setContent(boardDTO.getContent().trim());
        board.setAuthor(boardDTO.getAuthor().trim());

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            // 기존 파일 삭제
            if (board.getFilename() != null && !board.getFilename().isEmpty()) {
                deleteFile(board.getFilename());
            }
            String fileName = saveFile(file);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        boardRepository.save(board);
    }

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 게시글 목록
     */
    public Page<BoardDTO> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * 검색어로 게시글 목록을 조회합니다. (제목, 내용, 작성자 통합 검색)
     *
     * @param searchKeyword 검색어
     * @param searchType 검색 타입 (title, content, author, all)
     * @param pageable 페이징 정보
     * @return 검색어가 포함된 게시글 목록
     */
    public Page<BoardDTO> boardSearchList(String searchKeyword, String searchType, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return boardList(pageable);
        }

        String keyword = searchKeyword.trim();

        switch (searchType) {
            case "title":
                return boardRepository.findByTitleContaining(keyword, pageable).map(this::convertToDTO);
            case "content":
                return boardRepository.findByTitleContainingOrContentContaining("", keyword, pageable).map(this::convertToDTO);
            case "author":
                return boardRepository.findByAuthorContaining(keyword, pageable).map(this::convertToDTO);
            case "all":
            default:
                return boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable).map(this::convertToDTO);
        }
    }

    /**
     * 인기 게시물 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 인기 게시물 목록
     */
    public Page<BoardDTO> getPopularPosts(Pageable pageable) {
        return boardRepository.findPopularPosts(pageable).map(this::convertToDTO);
    }

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param id 게시글 ID
     */
    public void boardDelete(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다. ID: " + id));

        // 첨부 파일이 있는 경우 파일도 삭제
        if (board.getFilename() != null && !board.getFilename().isEmpty()) {
            deleteFile(board.getFilename());
        }

        boardRepository.deleteById(id);
    }

    /**
     * Board 엔티티를 BoardDTO로 변환하는 메서드
     *
     * @param board 게시물 엔티티
     * @return 변환된 BoardDTO
     */
    private BoardDTO convertToDTO(Board board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setAuthor(board.getAuthor());
        dto.setViewCount(board.getViewCount());
        dto.setFilename(board.getFilename());
        dto.setFilepath(board.getFilepath());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setUpdatedAt(board.getUpdatedAt());
        return dto;
    }

    /**
     * 게시물 데이터 유효성 검증
     *
     * @param boardDTO 검증할 게시물 DTO
     */
    private void validateBoardData(BoardDTO boardDTO) {
        if (boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }
        if (boardDTO.getContent() == null || boardDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
        }
        if (boardDTO.getAuthor() == null || boardDTO.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("작성자는 필수 입력 항목입니다.");
        }
        if (boardDTO.getTitle().length() > 200) {
            throw new IllegalArgumentException("제목은 200자 이하로 입력해주세요.");
        }
        if (boardDTO.getContent().length() > 4000) {
            throw new IllegalArgumentException("내용은 4000자 이하로 입력해주세요.");
        }
        if (boardDTO.getAuthor().length() > 50) {
            throw new IllegalArgumentException("작성자는 50자 이하로 입력해주세요.");
        }
    }

    /**
     * 파일을 저장하는 메서드
     *
     * @param file 첨부 파일
     * @return 저장된 파일의 이름
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 파일 저장 디렉토리 생성
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // UUID를 이용해 고유한 파일 이름 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File saveFile = new File(FILE_DIRECTORY, fileName);

        // 파일을 지정한 경로에 저장
        file.transferTo(saveFile);
        return fileName;
    }

    /**
     * 파일을 삭제하는 메서드
     *
     * @param filename 삭제할 파일명
     */
    private void deleteFile(String filename) {
        try {
            File file = new File(FILE_DIRECTORY, filename);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // 파일 삭제 실패는 로그만 남기고 진행
            System.err.println("파일 삭제 실패: " + filename + ", 오류: " + e.getMessage());
        }
    }

    /**
     * 파일 유효성 검증
     *
     * @param file 검증할 파일
     */
    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB 이하여야 합니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. 허용 형식: " + ALLOWED_EXTENSIONS);
        }
    }
}