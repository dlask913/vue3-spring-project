package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.LoginRequestDto;
import com.example.noticeboardservice.dto.LoginResponseDto;
import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.service.MemberService;
import com.example.noticeboardservice.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("회원 가입을 한다")
    public void registerMemberTest() throws Exception {
        // given
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        Mockito.when(memberServiceImpl.registerMember(Mockito.any())).thenReturn(1);

        // when // then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("회원 가입이 완료되었습니다."));

        Mockito.verify(memberServiceImpl, Mockito.times(1)).registerMember(Mockito.any());
    }

    @Test
    @DisplayName("회원이 로그인을 한다")
    public void loginMemberTest() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto();
        LoginResponseDto responseDto = new LoginResponseDto();
        Mockito.when(memberServiceImpl.login(Mockito.any())).thenReturn(responseDto);

        // when // then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        Mockito.verify(memberServiceImpl, Mockito.times(1)).login(Mockito.any());
    }

    @Test
    @DisplayName("회원 정보를 단일 조회한다.")
    public void findMemberTest() throws Exception {
        // given
        Long memberId = 1L;
        MemberResponseDto responseDto = new MemberResponseDto(1L, "limnj", "limnj@test.com", "1234", "memberImg", "address");
        Mockito.when(memberServiceImpl.findMember(memberId)).thenReturn(responseDto);

        // when // then
        mockMvc.perform(get("/member/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        Mockito.verify(memberServiceImpl, Mockito.times(1)).findMember(memberId);
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    public void updateMemberTest() throws Exception {
        // given
        Long memberId = 1L;
        MemberRequestDto requestDto = new MemberRequestDto();
        MockMultipartFile memberDtoPart = new MockMultipartFile("memberDto", "", "application/json", objectMapper.writeValueAsBytes(requestDto));
        MockMultipartFile memberImgPart = new MockMultipartFile("memberImg", "image.jpg", "image/jpeg", new byte[0]);
        Mockito.when(memberServiceImpl.updateMember(Mockito.any(), Mockito.any())).thenReturn(1);

        // when // then
        mockMvc.perform(multipart("/member/{memberId}", memberId)
                        .file(memberDtoPart)
                        .file(memberImgPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().string("수정 완료되었습니다."));

        Mockito.verify(memberServiceImpl, Mockito.times(1)).updateMember(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("회원 탈퇴를 한다.")
    public void deleteMemberTest() throws Exception {
        // given
        Long memberId = 1L;
        Mockito.when(memberServiceImpl.deleteMember(memberId)).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/member/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료되었습니다."));

        Mockito.verify(memberServiceImpl, Mockito.times(1)).deleteMember(memberId);
    }
}
