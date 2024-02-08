package com.example.devkorproject.customer.service;

import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.dto.GoogleLoginReq;
import com.example.devkorproject.customer.dto.LoginReq;
import com.example.devkorproject.customer.dto.LoginRes;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;

    public CustomerService(CustomerRepository customerRepository, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
    }

    public CustomerEntity searchByMemberId(Long customerId) {
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
        CustomerEntity customer = opCustomer.get();
        return customer;
    }


    public void saveFCMToken(Long customerId, String fcmToken){
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
        CustomerEntity customer = opCustomer.get();
        customer.setFcmToken(fcmToken);
    }

    public String searchFCMTokenByCustomerId(Long customerId){
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());

        Optional<String> opFcmToken = customerRepository.findFcmTokenByCustomerId(customerId);
        if(opFcmToken.isEmpty())
            throw new GeneralException(ErrorCode.FCMTOKEN_DOES_NOT_EXIST.getMessage());

        return opFcmToken.get();

    public LoginRes login(LoginReq loginReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByEmail(loginReq.getEmail());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerEntity customer = opCustomer.get();
        if (!encoder.matches(loginReq.getPassword(), customer.getPassword()))
            throw new GeneralException(ErrorCode.WRONG_PASSWORD);
        String accessToken= jwtUtil.createToken(customer.getCustomerId());
        return new LoginRes(accessToken);
    }
    public LoginRes googleLogin(GoogleLoginReq googleLoginReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByEmail(googleLoginReq.getEmail());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerEntity customer = opCustomer.get();
        String accessToken= jwtUtil.createToken(customer.getCustomerId());
        return new LoginRes(accessToken);

    }
}
