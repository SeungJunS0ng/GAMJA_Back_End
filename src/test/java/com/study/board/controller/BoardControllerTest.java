package com.study.board.controller;

import com.study.board.dto.BoardDTO;
import com.study.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@WithMockUser
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    private BoardDTO testBoard;

    @BeforeEach
    void setUp() {
        testBoard = BoardDTO.builder()
                .id(1)
                .title("테스트 제목")
                .content("테스트 내용")
                .author("테스트 작성자")
                .viewCount(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 메인페이지_리다이렉트_성공() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    void 게시물작성폼_조회_성공() throws Exception {
        mockMvc.perform(get("/board/write"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardwrite"))
                .andExpect(model().attributeExists("boardDTO"));
    }

    @Test
    void 게시물목록_조회_성공() throws Exception {
        Page<BoardDTO> mockPage = new PageImpl<>(Arrays.asList(testBoard));
        when(boardService.boardList(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/board/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardlist"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    void 게시물검색_성공() throws Exception {
        Page<BoardDTO> mockPage = new PageImpl<>(Arrays.asList(testBoard));
        when(boardService.boardSearchList(anyString(), anyString(), any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/board/list")
                .param("searchKeyword", "테스트")
                .param("searchType", "title"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardlist"));
    }

    @Test
    void 인기게시물_조회_성공() throws Exception {
        Page<BoardDTO> mockPage = new PageImpl<>(Arrays.asList(testBoard));
        when(boardService.getPopularPosts(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/board/popular"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardlist"));
    }

    @Test
    void 게시물상세보기_성공() throws Exception {
        when(boardService.boardView(1)).thenReturn(testBoard);

        mockMvc.perform(get("/board/view").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardview"))
                .andExpect(model().attribute("board", testBoard));
    }

    @Test
    void 게시물수정폼_조회_성공() throws Exception {
        when(boardService.boardViewWithoutIncrement(1)).thenReturn(testBoard);

        mockMvc.perform(get("/board/modify/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardmodify"));
    }

    @Test
    void REST_API_게시물목록조회_성공() throws Exception {
        Page<BoardDTO> mockPage = new PageImpl<>(Arrays.asList(testBoard));
        when(boardService.boardList(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void REST_API_게시물상세조회_성공() throws Exception {
        when(boardService.boardView(1)).thenReturn(testBoard);

        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void REST_API_인기게시물조회_성공() throws Exception {
        Page<BoardDTO> mockPage = new PageImpl<>(Arrays.asList(testBoard));
        when(boardService.getPopularPosts(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/boards/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
