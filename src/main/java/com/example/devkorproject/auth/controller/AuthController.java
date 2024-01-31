package com.example.devkorproject.auth.controller;

import com.example.devkorproject.auth.dto.RegisterReq;
import com.example.devkorproject.auth.dto.RegisterRes;
import com.example.devkorproject.auth.service.AuthService;
import com.example.devkorproject.common.dto.HttpDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("")
    public HttpDataResponse<RegisterRes> Register(@RequestBody RegisterReq registerReq){
        return HttpDataResponse.of(authService.register(registerReq));
    }
    @GetMapping("/checkName")
    public HttpDataResponse<Boolean> checkNickname(@RequestParam String customerName){
        return HttpDataResponse.of(authService.checkCustomerName(customerName));
    }
    @GetMapping("/checkEmail")
    public HttpDataResponse<Boolean> checkEmail(@RequestParam String email){
        return HttpDataResponse.of(authService.checkEmail(email));
    }
}
