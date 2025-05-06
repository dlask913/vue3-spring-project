package com.example.noticeboardservice.config;

import com.limnj.mail_sender.MailService;
import com.limnj.mail_sender.TraceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {
    @Bean
    public MailService mailService(JavaMailSender mailSender) {
        return new MailService(mailSender);
    }
    @Bean
    public TraceAspect traceAspect() {
        return new TraceAspect();
    }
}
