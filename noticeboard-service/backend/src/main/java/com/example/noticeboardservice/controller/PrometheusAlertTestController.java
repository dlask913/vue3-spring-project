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

    @GetMapping("/oom")
    public void oom() {
        List<byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new byte[50* 1024 * 1024]); // 50MB
        }
    }

    @GetMapping("/cpu")
    public String highCpu() {
        long end = System.currentTimeMillis() + 60000; // 60초 동안 CPU 태우기
        while (System.currentTimeMillis() < end) {
            Math.pow(Math.random(), Math.random());
        }
        return "CPU test done (60 seconds)";
    }
}
