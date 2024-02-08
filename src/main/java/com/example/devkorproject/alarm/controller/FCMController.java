package com.example.devkorproject.alarm.controller;

import com.example.devkorproject.alarm.dto.FCMMessageDto;
import com.example.devkorproject.alarm.dto.ReqDto;
import com.example.devkorproject.alarm.service.FCMService;
import com.example.devkorproject.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/fcm")
@RequiredArgsConstructor
public class FCMController {
    private final FCMService fcmService;
    private final CustomerService customerService;

    @PostMapping()
    public ResponseEntity sendMessageTo(@RequestBody ReqDto reqDto) throws IOException {
        String targetToken = customerService.searchFCMTokenByCustomerId(reqDto.getCustomerId());
        fcmService.sendMessageTo(
                targetToken,
                reqDto.getTitle(),
                reqDto.getBody()
        );

        return ResponseEntity.ok().build();
    }
}
