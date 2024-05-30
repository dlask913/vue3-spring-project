package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.HeartDto;
import com.example.noticeboardservice.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HeartController {
    private final HeartService heartServiceImpl;

    @GetMapping("/heart")
    public ResponseEntity<?> getHeartStatus(@RequestBody HeartDto heartDto){
        HeartDto response = heartServiceImpl.findHeart(heartDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/heart")
    public ResponseEntity<?> saveHeart(@RequestBody HeartDto heartDto) {
        int result = heartServiceImpl.saveHeart(heartDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/heart/{heartId}")
    public ResponseEntity<?> deleteHeart(@PathVariable("heartId") Long heartId) {
        int result = heartServiceImpl.deleteHeart(heartId);
        return ResponseEntity.ok().build();
    }
}
