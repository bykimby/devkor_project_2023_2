package com.example.devkorproject.baby.service;

import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.baby.dto.BabyModifyReqDto;
import com.example.devkorproject.baby.dto.BabyModifyResDto;
import com.example.devkorproject.baby.dto.BabyReqDto;
import com.example.devkorproject.baby.dto.BabyResDto;
import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.baby.exception.BabyDoesNotExistException;
import com.example.devkorproject.baby.repository.BabyRepository;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.exception.CustomerDoesNotExistException;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BabyService {
    private final BabyRepository babyRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    public BabyService(BabyRepository babyRepository, CustomerRepository customerRepository, JwtUtil jwtUtil) {
        this.babyRepository = babyRepository;
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
    }

    public BabyResDto enrollBaby(String token,BabyReqDto babyReqDto){
    //2023-09-26형태로 생일 string으로 받기
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customer=opCustomer.get();
        LocalDate babyBirth=LocalDate.parse(babyReqDto.getBirth(), DateTimeFormatter.ISO_DATE);
        BabyEntity babyEntity=BabyEntity.builder()
                .customer(customer)
                .babyName(babyReqDto.getBabyName())
                .birth(babyBirth)
                .allergy(babyReqDto.getAllergy())
                .needs(babyReqDto.getNeeds())
                .build();
        babyRepository.save(babyEntity);
        BabyResDto babyResDto=new BabyResDto(
                babyEntity.getBabyId(),
                babyEntity.getBabyName(),
                babyEntity.getBirth().toString(),
                babyEntity.getAllergy(),
                babyEntity.getNeeds());
        return babyResDto;
    }
    public BabyModifyResDto modifyBaby(String token,BabyModifyReqDto babyModifyReqDto){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<BabyEntity> OpBabyEntity=babyRepository.findBabyEntityByBabyId(babyModifyReqDto.getBabyId());
        if(OpBabyEntity.isEmpty())
            throw new BabyDoesNotExistException();
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customer=opCustomer.get();
        if(customer.getCustomerId()!= OpBabyEntity.get().getCustomer().getCustomerId())
            throw new CustomerDoesNotMatchException();
        LocalDate babyBirth=LocalDate.parse(babyModifyReqDto.getBirth(), DateTimeFormatter.ISO_DATE);
        BabyEntity babyEntity=OpBabyEntity.get();
        babyEntity.setBabyName(babyModifyReqDto.getBabyName());
        babyEntity.setBirth(babyBirth);
        babyEntity.setAllergy(babyModifyReqDto.getAllergy());
        babyEntity.setNeeds(babyModifyReqDto.getNeeds());
        BabyModifyResDto babyModifyResDto=new BabyModifyResDto(babyEntity.getBabyName(), babyModifyReqDto.getBirth(), babyEntity.getAllergy(), babyModifyReqDto.getNeeds());
        return babyModifyResDto;
    }
    public List<BabyModifyResDto> getCustomerBaby(String token){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<BabyEntity> babyEntities=babyRepository.findBabyByCustomerCustomerId(customerId);
        if(babyEntities.isEmpty())
            throw new BabyDoesNotExistException();
        for (BabyEntity babyEntity : babyEntities) {
            if (babyEntity.getCustomer().getCustomerId()!=customerId) {
                throw new CustomerDoesNotMatchException();
            }
        }
        return babyEntities.stream().map(baby -> {
            return new BabyModifyResDto(
                    baby.getBabyName(),
                    baby.getBirth().toString(),
                    baby.getAllergy(),
                    baby.getNeeds()
            );
        }).collect(Collectors.toList());
    }
}
