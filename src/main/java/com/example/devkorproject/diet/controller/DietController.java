package com.example.devkorproject.diet.controller;
import com.example.devkorproject.diet.dto.DietReqDto;
import com.example.devkorproject.diet.dto.DietResDto;
import com.example.devkorproject.diet.dto.SimpleReqDto;
import com.example.devkorproject.common.dto.HttpDataResponse;

import com.example.devkorproject.diet.dto.SimpleResDto;
import com.example.devkorproject.diet.service.DietService;
import lombok.*;

import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/diet")
@RestController
public class DietController {
    private final DietService dietService;

    @PostMapping("/simple")
    public HttpDataResponse<SimpleResDto[]> askQuestion(@RequestParam Long customerId, @RequestParam Long babyId ,@RequestBody SimpleReqDto simpleRequestDto){
        return HttpDataResponse.of(dietService.askQuestion(customerId, babyId, simpleRequestDto));
    }

    @PostMapping("/detail")
    public HttpDataResponse<DietResDto> getDetailDiet(@RequestParam Long customerId, @RequestParam Long babyId , @RequestBody DietReqDto dietRequestDto){
        return HttpDataResponse.of(dietService.getDetailDiet(customerId, babyId, dietRequestDto));
    }
}



