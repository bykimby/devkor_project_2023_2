package com.example.devkorproject.customer.controller;

import com.example.devkorproject.baby.dto.BabyReqDto;
import com.example.devkorproject.baby.dto.BabyResDto;
import com.example.devkorproject.common.dto.HttpDataResponse;
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
    @PostMapping("/temp/register")
    public HttpDataResponse<TempCustomer> TempRegister(@RequestBody TempCustomer tempCustomer){
        return HttpDataResponse.of(customerService.TempRegister(tempCustomer));
    }

    @PostMapping("/fcmToken")
    public void saveFCMToken(@RequestHeader Long customerId, @RequestBody String fcmToken){
        customerService.saveFCMToken(customerId, fcmToken);
    }

}
