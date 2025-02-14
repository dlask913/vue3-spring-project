package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.dto.MessageDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.MessageMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MessageServiceTest {
    @Autowired
    private MessageService messageServiceImpl;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        messageMapper.deleteAllMessages();
    }

    @Test
    @DisplayName("쪽지를 전송한다")
    void sendMessageTest() {
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageDto requestDto = MessageDto.builder()
                .content("쪽지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();

        // when
        messageServiceImpl.sendMessage(requestDto);

        // then
        MessageDto savedMessage = messageMapper.findAllMessages().get(0);
        assertThat(savedMessage.getContent()).isEqualTo(requestDto.getContent());
        assertThat(savedMessage.getSenderId()).isEqualTo(requestDto.getSenderId());
        assertThat(savedMessage.getReceiverId()).isEqualTo(requestDto.getReceiverId());
    }

    @Test
    @DisplayName("쪽지를 삭제한다")
    void deleteMessageTest(){
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageDto messageDto = MessageDto.builder()
                .content("쪽지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(messageDto);
        MessageDto savedMessage = messageMapper.findAllMessages().get(0);

        // when
        messageServiceImpl.deleteMessage(savedMessage.getId());

        // then
        assertThat(messageMapper.findAllMessages().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("쪽지를 상세 조회한다")
    void findMessageByIdTest(){
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageDto messageDto = MessageDto.builder()
                .content("쪽지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(messageDto);
        MessageDto savedMessage = messageMapper.findAllMessages().get(0);

        // when
        MessageDto findMessage = messageServiceImpl.findMessageByMessageId(savedMessage.getId());

        // then
        assertThat(findMessage.getContent()).isEqualTo(savedMessage.getContent());
        assertThat(findMessage.getSenderId()).isEqualTo(savedMessage.getSenderId());
        assertThat(findMessage.getReceiverId()).isEqualTo(savedMessage.getReceiverId());
    }


    private MemberResponseDto getMember(String email){
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findMemberByEmail(email);
        }
        return findMember;
    }

}