package com.example.devkorproject.baby.service;

import com.example.devkorproject.baby.dto.BabyModifyReqDto;
import com.example.devkorproject.baby.dto.BabyModifyResDto;
import com.example.devkorproject.baby.dto.BabyReqDto;
import com.example.devkorproject.baby.dto.BabyResDto;
import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.baby.exception.BabyDoesNotExistException;
import com.example.devkorproject.baby.repository.BabyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class BabyService {
    private final BabyRepository babyRepository;

    public BabyService(BabyRepository babyRepository) {
        this.babyRepository = babyRepository;
    }

    public BabyResDto enrollBaby(BabyReqDto babyReqDto){
    //2023-09-26형태로 생일 string으로 받기
        LocalDate babyBirth=LocalDate.parse(babyReqDto.getBirth(), DateTimeFormatter.ISO_DATE);
        BabyEntity babyEntity=BabyEntity.builder()
                .babyName(babyReqDto.getBabyName())
                .birth(babyBirth)
                .allergy(babyReqDto.getAllergy())
                .needs(babyReqDto.getNeeds())
                .build();
        babyRepository.save(babyEntity);
        BabyResDto babyResDto=new BabyResDto();
        return babyResDto;
    }
    public BabyModifyResDto modifyBaby(BabyModifyReqDto babyModifyReqDto){
        Optional<BabyEntity> OpBabyEntity=babyRepository.findBabyEntityByBabyId(babyModifyReqDto.getBabyId());
        if(OpBabyEntity.isEmpty())
            throw new BabyDoesNotExistException();
        LocalDate babyBirth=LocalDate.parse(babyModifyReqDto.getBirth(), DateTimeFormatter.ISO_DATE);
        BabyEntity babyEntity=OpBabyEntity.get();
        babyEntity.setBabyName(babyModifyReqDto.getBabyName());
        babyEntity.setBirth(babyBirth);
        babyEntity.setAllergy(babyModifyReqDto.getAllergy());
        babyEntity.setNeeds(babyModifyReqDto.getNeeds());
        BabyModifyResDto babyModifyResDto=new BabyModifyResDto();
        return babyModifyResDto;
    }
}
