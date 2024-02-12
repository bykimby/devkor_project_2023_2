package com.example.devkorproject.diet.controller;
import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.diet.dto.*;
import com.example.devkorproject.common.dto.HttpDataResponse;

import com.example.devkorproject.diet.service.DietService;
import lombok.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RequestMapping("/diet")
@RestController
public class DietController {
    private final DietService dietService;
    private final JwtUtil jwtUtil;
    @PostMapping("/simple")
    public HttpDataResponse<SimpleResDto[]> askQuestion(@RequestHeader("Authorization") String authHeader, @RequestParam Long babyId ,@RequestBody SimpleReqDto simpleRequestDto){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(dietService.askQuestion(customerId, babyId, simpleRequestDto));
    }

    @PostMapping("/detail")
    public HttpDataResponse<DietResDto> getDetailDiet(@RequestParam Long simpleDietId, @RequestBody DietReqDto dietRequestDto){
        return HttpDataResponse.of(dietService.getDetailDiet(simpleDietId, dietRequestDto));
    }

    @GetMapping("/fridge")
    public HttpDataResponse<List<FridgeSimpleDietResDto>> getFridgeSimpleDiet(@RequestHeader("Authorization") String authHeader, @RequestParam Long babyId, @RequestParam String type){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(dietService.getFridgeSimpleDiet(customerId, babyId, type));
    }

    @PostMapping("/fridge/detail")
    public HttpDataResponse<DietResDto> getFridgeDiet(@RequestParam Long simpleDietId, @RequestBody DietReqDto dietRequestDto){
        return HttpDataResponse.of(dietService.getDetailDiet(simpleDietId, dietRequestDto));
    }

    @PutMapping("/press")
    public HttpDataResponse<PressDto> pressHeart (@RequestParam Long simpleDietId){
        return HttpDataResponse.of(dietService.pressHeart(simpleDietId));
    }

    @GetMapping("/heart")
    public HttpDataResponse<List<HeartDietResDto>> getHeartDiet(@RequestHeader("Authorization") String authHeader){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(dietService.getHeartDiet(customerId));
    }

    @GetMapping("/heart/view")
    public HttpDataResponse<DietResDto> getHeartDietView(@RequestParam Long dietId){
        return HttpDataResponse.of(dietService.getHeartDietView(dietId));
    }

}



