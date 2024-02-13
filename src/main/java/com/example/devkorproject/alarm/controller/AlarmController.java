package com.example.devkorproject.alarm.controller;

import com.example.devkorproject.alarm.dto.AlarmResDto;
import com.example.devkorproject.alarm.dto.ReqDto;
import com.example.devkorproject.alarm.service.AlarmService;
import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController("/fcm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;


    @PostMapping() //테스트용
    public ResponseEntity sendMessageTo(@RequestBody ReqDto reqDto) throws IOException {
        String targetToken = customerService.searchFCMTokenByCustomerId(reqDto.getCustomerId());
        alarmService.sendMessageTo(
                targetToken,
                reqDto.getTitle(),
                reqDto.getBody()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public HttpDataResponse<List<AlarmResDto>> getRecentAlarm(@RequestHeader("Authorization") String authHeader){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);

        return HttpDataResponse.of(alarmService.getRecentAlarm(customerId));
    }
}
