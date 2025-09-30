package com.study.board.service;

import com.study.board.dto.BoardDTO;
import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private BoardService boardService;

    private Board testBoard;
    private BoardDTO testBoardDTO;

    @BeforeEach
    void setUp() {
        testBoard = new Board();
        testBoard.setId(1);
        testBoard.setTitle("테스트 제목");
        testBoard.setContent("테스트 내용");
        testBoard.setFilename("test.txt");
        testBoard.setFilepath("/files/test.txt");
        testBoard.setCreatedAt(LocalDateTime.now());
        testBoard.setUpdatedAt(LocalDateTime.now());

        testBoardDTO = new BoardDTO();
        testBoardDTO.setId(1);
        testBoardDTO.setTitle("테스트 제목");
        testBoardDTO.setContent("테스트 내용");
        testBoardDTO.setFilename("test.txt");
        testBoardDTO.setFilepath("/files/test.txt");
    }

    @Test
    void boardView_성공() {
        // Given
        when(boardRepository.findById(1)).thenReturn(Optional.of(testBoard));

        // When
        BoardDTO result = boardService.boardView(1);

        // Then
        assertNotNull(result);
        assertEquals(testBoard.getId(), result.getId());
        assertEquals(testBoard.getTitle(), result.getTitle());
        assertEquals(testBoard.getContent(), result.getContent());
        verify(boardRepository, times(1)).findById(1);
    }

    @Test
    void boardView_존재하지않는게시물_예외발생() {
        // Given
        when(boardRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            boardService.boardView(999);
        });
        verify(boardRepository, times(1)).findById(999);
    }

    @Test
    void write_파일없이_성공() throws Exception {
        // Given
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        // When
        boardService.write(testBoardDTO, null);

        // Then
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    void write_빈제목_예외발생() {
        // Given
        testBoardDTO.setTitle("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.write(testBoardDTO, null);
        });
    }

    @Test
    void write_null제목_예외발생() {
        // Given
        testBoardDTO.setTitle(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.write(testBoardDTO, null);
        });
    }

    @Test
    void write_빈내용_예외발생() {
        // Given
        testBoardDTO.setContent("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.write(testBoardDTO, null);
        });
    }

    @Test
    void boardList_성공() {
        // Given
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards);
        Pageable pageable = PageRequest.of(0, 10);
        when(boardRepository.findAll(pageable)).thenReturn(boardPage);

        // When
        Page<BoardDTO> result = boardService.boardList(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testBoard.getTitle(), result.getContent().get(0).getTitle());
        verify(boardRepository, times(1)).findAll(pageable);
    }

    @Test
    void boardSearchList_검색어있음_성공() {
        // Given
        String searchKeyword = "테스트";
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards);
        Pageable pageable = PageRequest.of(0, 10);
        when(boardRepository.findByTitleContaining(searchKeyword, pageable)).thenReturn(boardPage);

        // When
        Page<BoardDTO> result = boardService.boardSearchList(searchKeyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(boardRepository, times(1)).findByTitleContaining(searchKeyword, pageable);
    }

    @Test
    void boardSearchList_빈검색어_전체목록반환() {
        // Given
        String searchKeyword = "";
        List<Board> boards = Arrays.asList(testBoard);
        Page<Board> boardPage = new PageImpl<>(boards);
        Pageable pageable = PageRequest.of(0, 10);
        when(boardRepository.findAll(pageable)).thenReturn(boardPage);

        // When
        Page<BoardDTO> result = boardService.boardSearchList(searchKeyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(boardRepository, times(1)).findAll(pageable);
    }

    @Test
    void boardDelete_성공() {
        // Given
        when(boardRepository.findById(1)).thenReturn(Optional.of(testBoard));

        // When
        boardService.boardDelete(1);

        // Then
        verify(boardRepository, times(1)).findById(1);
        verify(boardRepository, times(1)).deleteById(1);
    }

    @Test
    void boardDelete_존재하지않는게시물_예외발생() {
        // Given
        when(boardRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            boardService.boardDelete(999);
        });
        verify(boardRepository, times(1)).findById(999);
        verify(boardRepository, never()).deleteById(999);
    }

    @Test
    void updateBoard_성공() throws Exception {
        // Given
        when(boardRepository.findById(1)).thenReturn(Optional.of(testBoard));
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        testBoardDTO.setTitle("수정된 제목");
        testBoardDTO.setContent("수정된 내용");

        // When
        boardService.updateBoard(1, testBoardDTO, null);

        // Then
        verify(boardRepository, times(1)).findById(1);
        verify(boardRepository, times(1)).save(any(Board.class));
    }
}
