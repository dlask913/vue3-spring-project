package com.limnj.mail_sender.service;

import com.limnj.mail_sender.aop.TraceAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class MailServiceTest {
    @MockBean // Deprecated
    private JavaMailSender mailSender;
    @Autowired
    private MailService mailService;

    @Test
    @DisplayName("메일을 전송하고, 로그를 통해 수신자를 확인할 수 있다.")
    void sendMailTest() {
        // Given
        String to = "limnj@test.com";
        String subject = "Subject";
        String body = "Body";

        // When
        mailService.sendMail(to, subject, body);

        // Then
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getTo()).containsExactly(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(body);
    }
}