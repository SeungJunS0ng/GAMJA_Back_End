package com.study.board.controller;

import com.study.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Test
    void 메인페이지_리다이렉트_성공() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    @Test
    void testBoardList() throws Exception {
        mockMvc.perform(get("/board/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardlist"));
    }

    @Test
    void testBoardWriteForm() throws Exception {
        mockMvc.perform(get("/board/write"))
                .andExpect(status().isOk())
                .andExpect(view().name("boardwrite"));
    }
}
