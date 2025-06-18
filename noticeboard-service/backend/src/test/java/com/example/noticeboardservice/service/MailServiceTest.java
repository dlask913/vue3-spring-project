package com.example.noticeboardservice.service;

import com.example.noticeboardservice.config.MailConfig;
import com.limnj.mail_sender.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.TestPropertySource;


//@SpringBootTest
public class MailServiceTest {

//    @Autowired
//    private MailService mailService;

    @Test
    @DisplayName("메일을 전송하고, 로그를 통해 수신자를 확인할 수 있다.")
    void sendMailTest() {
        // Given
        String to = "limnj@test.com";
        String subject = "Subject";
        String body = "Body";

        // When
//        mailService.sendMail(to, subject, body);
    }

    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(MailConfig.class);

        //1. 조회: 호출할 때 마다 같은 객체를 반환
        MailService mailService1 = ac.getBean("mailService", MailService.class);

        //2. 조회: 호출할 때 마다 같은 객체를 반환
        MailService mailService2 = ac.getBean("mailService", MailService.class);

        //참조값이 같은 것을 확인
        System.out.println("mailService1 = " + mailService1);
        System.out.println("mailService2 = " + mailService2);
        //memberService1 == memberService2 assertThat(memberService1).isSameAs(memberService2);
    }
}
