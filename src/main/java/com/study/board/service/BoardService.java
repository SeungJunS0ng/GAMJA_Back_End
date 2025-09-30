package com.study.board.service;

import com.study.board.dto.BoardDTO;
import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Value("${file.upload.directory:${user.home}/board-files}")
    private String uploadDirectory;

    // 게시글 목록 조회 (페이징)
    public Page<BoardDTO> boardList(Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        return boards.map(this::convertToDTO);
    }

    // 게시글 검색 (페이징)
    public Page<BoardDTO> boardSearchList(String searchKeyword, String searchType, Pageable pageable) {
        Page<Board> boards;

        switch (searchType) {
            case "title":
                boards = boardRepository.findByTitleContaining(searchKeyword, pageable);
                break;
            case "author":
                boards = boardRepository.findByAuthorContaining(searchKeyword, pageable);
                break;
            case "content":
                boards = boardRepository.findByTitleContainingOrContentContaining(searchKeyword, searchKeyword, pageable);
                break;
            default: // "all"
                boards = boardRepository.findByTitleContainingOrContentContaining(searchKeyword, searchKeyword, pageable);
                break;
        }

        return boards.map(this::convertToDTO);
    }

    // 게시글 작성
    public BoardDTO write(BoardDTO boardDTO, MultipartFile file) throws Exception {
        log.info("게시글 작성 서비스 - 제목: {}", boardDTO.getTitle());

        Board board = convertToEntity(boardDTO);

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            String[] fileInfo = saveFile(file);
            board.setFilename(fileInfo[0]);
            board.setFilepath(fileInfo[1]);
        }

        Board savedBoard = boardRepository.save(board);
        log.info("게시글 저장 완료 - ID: {}", savedBoard.getId());

        return convertToDTO(savedBoard);
    }

    // 게시글 상세 조회 (조회수 증가)
    @Transactional
    public BoardDTO boardView(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));

        // 조회수 증가
        boardRepository.increaseViewCount(id);
        board.setViewCount(board.getViewCount() + 1);

        return convertToDTO(board);
    }

    // 게시글 조회 (조회수 증가 없음) - 수정 폼용
    public BoardDTO boardViewWithoutIncrement(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));

        return convertToDTO(board);
    }

    // 게시글 삭제
    public void boardDelete(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));

        // 첨부파일 삭제
        if (board.getFilepath() != null) {
            deleteFile(board.getFilepath());
        }

        boardRepository.deleteById(id);
        log.info("게시글 삭제 완료 - ID: {}", id);
    }

    // 게시글 수정
    public BoardDTO updateBoard(Integer id, BoardDTO boardDTO, MultipartFile file) throws Exception {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id));

        // 기본 정보 업데이트
        existingBoard.setTitle(boardDTO.getTitle());
        existingBoard.setContent(boardDTO.getContent());
        existingBoard.setAuthor(boardDTO.getAuthor());

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            // 기존 파일 삭제
            if (existingBoard.getFilepath() != null) {
                deleteFile(existingBoard.getFilepath());
            }

            // 새 파일 저장
            String[] fileInfo = saveFile(file);
            existingBoard.setFilename(fileInfo[0]);
            existingBoard.setFilepath(fileInfo[1]);
        }

        Board updatedBoard = boardRepository.save(existingBoard);
        log.info("게시글 수정 완료 - ID: {}", id);

        return convertToDTO(updatedBoard);
    }

    // 인기 게시글 조회
    public Page<BoardDTO> getPopularPosts(Pageable pageable) {
        Page<Board> boards = boardRepository.findPopularPosts(pageable);
        return boards.map(this::convertToDTO);
    }

    // 파일 저장
    private String[] saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일명 생성 (UUID + 원본 파일명)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = Paths.get(uploadDirectory, savedFilename);
        Files.copy(file.getInputStream(), filePath);

        log.info("파일 저장 완료 - 원본: {}, 저장: {}", originalFilename, savedFilename);

        return new String[]{originalFilename, filePath.toString()};
    }

    // 파일 삭제
    private void deleteFile(String filepath) {
        try {
            Path path = Paths.get(filepath);
            Files.deleteIfExists(path);
            log.info("파일 삭제 완료 - {}", filepath);
        } catch (IOException e) {
            log.error("파일 삭제 실패 - {}: {}", filepath, e.getMessage());
        }
    }

    // Entity to DTO 변환
    private BoardDTO convertToDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .author(board.getAuthor())
                .viewCount(board.getViewCount())
                .filename(board.getFilename())
                .filepath(board.getFilepath())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    // DTO to Entity 변환
    private Board convertToEntity(BoardDTO boardDTO) {
        return Board.builder()
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .author(boardDTO.getAuthor())
                .viewCount(0)
                .build();
    }
}
