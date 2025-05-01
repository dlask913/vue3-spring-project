package com.limnj.mail_sender.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TraceAspect {
    @Before("@annotation(com.limnj.mail_sender.aop.Trace)")
    public void doTrace(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("[MailService] {} 으로 메일 전송이 완료되었습니다.", args[0]);
    }
}
