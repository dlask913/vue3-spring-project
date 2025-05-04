package com.limnj.mail_sender.config;

import com.limnj.mail_sender.aop.TraceAspect;
import com.limnj.mail_sender.service.MailService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration
@ConditionalOnProperty(
        name = {"memory"},
        havingValue = "on"
)
public class MemoryAutoConfig {
    @Bean
    public TraceAspect traceAspect() {
        return new TraceAspect();
    }
    @Bean
    public MailService mailService(JavaMailSender mailSender) {
        return new MailService(mailSender);
    }
}
