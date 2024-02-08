package com.example.devkorproject.customer.controller;

import com.example.devkorproject.baby.dto.BabyReqDto;
import com.example.devkorproject.baby.dto.BabyResDto;
import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.customer.dto.GoogleLoginReq;
import com.example.devkorproject.customer.dto.LoginReq;
import com.example.devkorproject.customer.dto.LoginRes;
import com.example.devkorproject.customer.dto.TempCustomer;
import com.example.devkorproject.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    //Todo: 로그인, 탈퇴, 회원 정보 수정, 로그아웃, 좋아요, post수 카운트, 등업조건 count
    private final CustomerService customerService;
    @PostMapping("/login")
    public HttpDataResponse<LoginRes> login(@RequestBody LoginReq loginReq){
        return HttpDataResponse.of(customerService.login(loginReq));
    }


    @PostMapping("/fcmToken")
    public void saveFCMToken(@RequestHeader Long customerId, @RequestBody String fcmToken){
        customerService.saveFCMToken(customerId, fcmToken);

    @PostMapping("/googleLogin")
    public HttpDataResponse<LoginRes> googleLogin(@RequestBody GoogleLoginReq googleLoginReq){
        return HttpDataResponse.of(customerService.googleLogin(googleLoginReq));

    }

}
