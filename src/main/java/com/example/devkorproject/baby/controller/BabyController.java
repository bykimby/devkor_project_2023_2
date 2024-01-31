package com.example.devkorproject.baby.controller;

import com.example.devkorproject.baby.dto.BabyModifyReqDto;
import com.example.devkorproject.baby.dto.BabyModifyResDto;
import com.example.devkorproject.baby.dto.BabyReqDto;
import com.example.devkorproject.baby.dto.BabyResDto;
import com.example.devkorproject.baby.service.BabyService;
import com.example.devkorproject.common.dto.HttpDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/customer/baby")
@RestController
public class BabyController {
    private final BabyService babyService;
    //Todo
    //아기 등록, 아기 정보 수정
    @PostMapping("/enroll")
    public HttpDataResponse<BabyResDto> enrollBaby(@RequestHeader("Authorization") String authHeader,@RequestBody BabyReqDto babyReqDto){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(babyService.enrollBaby(token,babyReqDto));
    }

    @PutMapping("")
    public HttpDataResponse<BabyModifyResDto> modifyBaby(@RequestHeader("Authorization") String authHeader,@RequestBody BabyModifyReqDto babyModifyReqDto){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(babyService.modifyBaby(token,babyModifyReqDto));
    }
    @GetMapping("")
    public HttpDataResponse<List<BabyModifyResDto>> getCustomerBaby(@RequestHeader("Authorization") String authHeader){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(babyService.getCustomerBaby(token));
    }
}
