package com.example.devkorproject.fridge.controller;

import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.fridge.dto.CustomerFridgeReq;
import com.example.devkorproject.fridge.dto.FridgeDto;
import com.example.devkorproject.fridge.dto.FridgeResDto;
import com.example.devkorproject.fridge.dto.FridgeUpReq;
import com.example.devkorproject.fridge.service.FridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fridge")
public class FridgeController {
    private final FridgeService fridgeService;
    @PostMapping()
    public HttpDataResponse<FridgeResDto> createFridge(@RequestBody FridgeDto fridgeDto){
        return HttpDataResponse.of(fridgeService.createFridge(fridgeDto));
    }
    @GetMapping("/customer")
    public HttpDataResponse<List<FridgeResDto>> getCustomerFridge(@RequestHeader("customerId") Long customerId){
        return HttpDataResponse.of(fridgeService.getCustomerFridge(customerId));
    }
    @PutMapping()
    public HttpDataResponse<FridgeResDto> updateFridge(@RequestBody FridgeUpReq fridgeUpReq){
        return HttpDataResponse.of(fridgeService.updateFridge(fridgeUpReq));
    }
}
