package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.dto.RoomDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.RoomMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class RoomServiceTest {

    @Autowired
    RoomService roomServiceImpl;
    @Autowired
    RoomMapper roomMapper;

    @Autowired
    MemberMapper memberMapper;

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
        assertThat(findRoom.getId()).isEqualTo(roomDto.getId());
        assertThat(findRoom.getLowerId()).isEqualTo(roomDto.getLowerId());
        assertThat(findRoom.getHigherId()).isEqualTo(roomDto.getHigherId());
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
        assertThat(findRoom.getId()).isEqualTo(roomDto.getId());
        assertThat(findRoom.getHigherId()).isEqualTo(roomDto.getHigherId());
        assertThat(findRoom.getLowerId()).isEqualTo(roomDto.getLowerId());
    }

    @Test
    @DisplayName("회원이 속한 채팅방들을 모두 조회한다.")
    void findRoomsByMemberIdTest() {
        // given
        Long memberAId = getMemberId("limnjA@test.com");
        Long memberBId = getMemberId("limnjB@test.com");
        Long memberCId = getMemberId("limnjC@test.com");
        RoomDto roomAB = RoomDto.builder()
                .lowerId(min(memberAId, memberBId))
                .higherId(max(memberAId, memberBId))
                .build();
        RoomDto roomAC = RoomDto.builder()
                .lowerId(min(memberAId, memberCId))
                .higherId(max(memberAId, memberCId))
                .build();
        roomServiceImpl.insertRoom(roomAB);
        roomServiceImpl.insertRoom(roomAC);

        // when
        List<RoomDto> rooms = roomServiceImpl.findRoomsByMemberId(memberAId);

        // then
        assertThat(rooms).hasSize(2);

        RoomDto firstRoom = rooms.get(0); // 첫번째 방 (A, B)
        assertThat(firstRoom.getLowerId()).isEqualTo(min(memberAId, memberBId));
        assertThat(firstRoom.getHigherId()).isEqualTo(max(memberAId, memberBId));
        assertThat(firstRoom.getLowerIdUsername()).isNotBlank();
        assertThat(firstRoom.getHigherIdUsername()).isNotBlank();

        RoomDto secondRoom = rooms.get(1); // 두번째 방 (A, C)
        assertThat(secondRoom.getLowerId()).isEqualTo(min(memberAId, memberCId));
        assertThat(secondRoom.getHigherId()).isEqualTo(max(memberAId, memberCId));
        assertThat(secondRoom.getLowerIdUsername()).isNotBlank();
        assertThat(secondRoom.getHigherIdUsername()).isNotBlank();
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