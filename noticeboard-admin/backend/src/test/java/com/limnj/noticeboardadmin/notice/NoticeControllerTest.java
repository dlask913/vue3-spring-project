package com.limnj.noticeboardadmin.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limnj.noticeboardadmin.config.filter.JwtTokenFilter;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoticeController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class NoticeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoticeService noticeServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Test
    @DisplayName("PDF 파일과 함께 게시글을 저장한다")
    void saveNoticeTest() throws Exception {
        // given
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("title")
                .content("content")
                .memberId(1L)
                .build();
        MockMultipartFile noticeFile = new MockMultipartFile(
                "noticeFile", // Controller 파라미터명
                "test.pdf",
                "application/pdf",
                "<<pdf content>>".getBytes()
        );
        MockMultipartFile noticeRequestDto = new MockMultipartFile(
                "noticeRequestDto", "", "application/json",
                objectMapper.writeValueAsBytes(requestDto)
        );

        Mockito.when(noticeServiceImpl.saveNotice(any(NoticeRequestDto.class), any(MockMultipartFile.class))).thenReturn(1);

        // when // then
        mockMvc.perform(multipart("/notice")
                        .file(noticeFile)
                        .file(noticeRequestDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글 저장이 완료되었습니다."));

        Mockito.verify(noticeServiceImpl, Mockito.times(1))
                .saveNotice(any(NoticeRequestDto.class), any(MockMultipartFile.class));
    }
}