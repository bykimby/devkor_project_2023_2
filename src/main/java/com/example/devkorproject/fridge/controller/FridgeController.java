package com.example.devkorproject.fridge.controller;

import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.fridge.dto.*;
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
    public HttpDataResponse<FridgeResFull> createFridge(@RequestHeader("Authorization") String authHeader,@RequestBody FridgeDto fridgeDto){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.createFridge(token,fridgeDto));
    }
    @PostMapping("/customer/sort")
    public HttpDataResponse<List<FridgeResDto>> saveCustomerOrder(@RequestHeader("Authorization") String authHeader,@RequestBody CustomerSortReq customerSortReq){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.saveCustomerOrder(token,customerSortReq));
    }
    @GetMapping("/customer")
    public HttpDataResponse<List<FridgeResDto>> getCustomerFridge(@RequestHeader("Authorization") String authHeader){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.getCustomerFridge(token));
    }
    @GetMapping("/customer/new")
    public HttpDataResponse<List<FridgeResDto>> getCustomerFridgeNew(@RequestHeader("Authorization") String authHeader){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.getCustomerFridgeNew(token));
    }
    @GetMapping("/customer/old")
    public HttpDataResponse<List<FridgeResDto>> getCustomerFridgeOld(@RequestHeader("Authorization") String authHeader){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.getCustomerFridgeOld(token));
    }
    @GetMapping("/customer/unique")
    public HttpDataResponse<FridgeResFull> getCustomerFridgeUnique(@RequestHeader("Authorization") String authHeader,@RequestParam Long fridgeId){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.getCustomerFridgeUnique(token,fridgeId));
    }
    @PutMapping()
    public HttpDataResponse<FridgeResFull> updateFridge(@RequestHeader("Authorization") String authHeader,@RequestBody FridgeUpReq fridgeUpReq){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(fridgeService.updateFridge(token,fridgeUpReq));
    }
    @DeleteMapping()
    public void deleteFridge(@RequestHeader("Authorization") String authHeader,@RequestBody DeleteFridgeReq deleteFridgeReq){
        String token=authHeader.substring(7);
        fridgeService.deleteFridge(token,deleteFridgeReq);
    }
}
