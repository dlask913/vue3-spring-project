package com.example.noticeboardservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PrometheusAlertTestController {
    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(5000); // 5초
        return "done";
    }
}
