package com.example.devkorproject.customer.controller;

import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.baby.dto.BabyReqDto;
import com.example.devkorproject.baby.dto.BabyResDto;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.dto.*;
import com.example.devkorproject.customer.service.CustomerService;
import com.example.devkorproject.post.dto.CommentRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public HttpDataResponse<LoginRes> login(@RequestBody LoginReq loginReq){
        return HttpDataResponse.of(customerService.login(loginReq));
    }
    @PostMapping("/googleLogin")
    public HttpDataResponse<LoginRes> googleLogin(@RequestBody GoogleLoginReq googleLoginReq){
        return HttpDataResponse.of(customerService.googleLogin(googleLoginReq));
    }
    @GetMapping("/my")
    public HttpDataResponse<GetMyPageRes> getMyPage(@RequestHeader("authorization") String authHeader){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.getMyPage(customerId));
    }


    @PutMapping("/nickname")
    public HttpDataResponse<Boolean> changeCustomerName(@RequestHeader("authorization") String authHeader,@RequestBody ChangeCustomerNameReq changeCustomerNameReq){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.changeCustomerName(customerId,changeCustomerNameReq));
    }

    @PostMapping("/password/validate")
    public HttpDataResponse<Boolean> validatePassword(@RequestHeader("authorization") String authHeader,@RequestBody ValidatePasswordReq validatePasswordReq){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.validatePassword(customerId,validatePasswordReq));
    }
    @PutMapping("/password")
    public HttpDataResponse<Boolean> changePassword(@RequestHeader("authorization") String authHeader,@RequestBody ChangePasswordReq changePasswordReq){
        String token=authHeader.substring(7);
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.changePassword(customerId,changePasswordReq));
    }
    @GetMapping("/manage")
    public HttpDataResponse<ManageRes> getManage(@RequestHeader("authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId = jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.getManage(customerId));
    }
    @GetMapping("/myComments")
    public HttpDataResponse<List<MyCommentRes>> getMyComments(@RequestHeader("authorization") String authHeader){
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId = jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.getMyComments(customerId));
    }
    @GetMapping("/myPosts")
    public HttpDataResponse<List<MyPostRes>> getMyPosts(@RequestHeader("authorization") String authHeader){
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId = jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.getMyPosts(customerId));
    }
    @DeleteMapping("")
    public HttpDataResponse<Boolean> withdraw(@RequestHeader("authorization") String authHeader){
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId = jwtUtil.getCustomerIdFromToken(token);
        return HttpDataResponse.of(customerService.withdraw(customerId));
    }
}
