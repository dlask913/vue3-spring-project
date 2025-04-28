package com.limnj.mail_sender.controller;

import com.limnj.mail_sender.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send-mail")
    public void testSendMail(){
        mailService.sendMail("email", "Mail", "Mail Test");
    }
}
