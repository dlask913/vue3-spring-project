package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.dto.RoomDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.RoomMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SpringBootTest
@ActiveProfiles("test")
class RoomServiceTest {
    @Autowired
    RoomService roomServiceImpl;
    @Autowired
    RoomMapper roomMapper;

    @Autowired
    MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        roomMapper.deleteAllRooms();
    }

    @Test
    @DisplayName("멤버 간 채팅방이 없을 때 메시지 전송 시 채팅방이 자동 생성된다.")
    void insertRoomTest(){
        // given
        Long senderId = getMemberId("limnjSender@test.com");
        Long receiverId = getMemberId("limnjReceiver@test.com");
        RoomDto roomDto = RoomDto.builder()
                .lowerId(min(senderId, receiverId))
                .higherId(max(senderId, receiverId))
                .build();

        // when
        roomServiceImpl.insertRoom(roomDto);

        // then
        RoomDto findRoom = roomMapper.findRoomByMembers(senderId, receiverId);// senderId와 receiverId 당 1개
        Assertions.assertThat(findRoom.getId()).isEqualTo(roomDto.getId());
        Assertions.assertThat(findRoom.getLowerId()).isEqualTo(roomDto.getLowerId());
        Assertions.assertThat(findRoom.getHigherId()).isEqualTo(roomDto.getHigherId());
    }

    @Test
    @DisplayName("sender 와 receiver 구분없이 멤버 둘의 ID로 채팅방 하나를 조회한다.")
    void findRoomByMembersTest(){
        // given
        Long memberAId = getMemberId("limnjSender@test.com");
        Long memberBId = getMemberId("limnjReceiver@test.com");
        RoomDto roomDto = RoomDto.builder()
                .lowerId(min(memberAId, memberBId))
                .higherId(max(memberAId, memberBId))
                .build();
        roomServiceImpl.insertRoom(roomDto);

        // when
        RoomDto findRoom = roomServiceImpl.findRoomByMembers(memberAId, memberBId).orElseThrow();

        // then
        Assertions.assertThat(findRoom.getId()).isEqualTo(roomDto.getId());
        Assertions.assertThat(findRoom.getHigherId()).isEqualTo(roomDto.getHigherId());
        Assertions.assertThat(findRoom.getLowerId()).isEqualTo(roomDto.getLowerId());
    }

    private Long getMemberId(String email){
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberRequestDto.getId();
        }
        return findMember.id();
    }
}