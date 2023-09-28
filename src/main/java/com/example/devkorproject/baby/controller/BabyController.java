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

@RequiredArgsConstructor
@RequestMapping("/customer/baby")
@RestController
public class BabyController {
    private final BabyService babyService;
    //Todo
    //아기 등록, 아기 정보 수정
    @PostMapping("/enroll")
    public HttpDataResponse<BabyResDto> enrollBaby(@RequestBody BabyReqDto babyReqDto){
        return HttpDataResponse.of(babyService.enrollBaby(babyReqDto));
    }

    @PutMapping("")
    public HttpDataResponse<BabyModifyResDto> modifyBaby(@RequestBody BabyModifyReqDto babyModifyReqDto){
        return HttpDataResponse.of(babyService.modifyBaby(babyModifyReqDto));
    }
}
