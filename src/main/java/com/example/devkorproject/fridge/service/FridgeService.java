package com.example.devkorproject.fridge.service;

import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.fridge.dto.*;
import com.example.devkorproject.fridge.entity.FridgeEntity;
import com.example.devkorproject.fridge.repository.FridgeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FridgeService {
    private final FridgeRepository fridgeRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    public FridgeService(FridgeRepository fridgeRepository, CustomerRepository customerRepository, JwtUtil jwtUtil) {
        this.fridgeRepository = fridgeRepository;
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
    }

    public FridgeResFull createFridge(String token,FridgeDto fridgeDto){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        FridgeEntity fridgeEntity=FridgeEntity.builder()
                                    .customer(customer)
                                    .date(LocalDateTime.now())
                                    .ingredients(fridgeDto.getIngredients())
                                    .active(fridgeDto.isActive())
                                    .emoticon(fridgeDto.getEmoticon())
                                    .build();
        fridgeRepository.save(fridgeEntity);
        return new FridgeResFull(fridgeEntity.getFrigeId(),fridgeEntity.getIngredients(), fridgeEntity.isActive(), fridgeEntity.getEmoticon());
    }
    public List<FridgeResDto> getCustomerFridge(String token){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerIdOrderByCustomerOrderAsc(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        return fridgeEntities.stream().map(fridge -> new FridgeResDto(
                fridge.getFrigeId(),
                fridge.getIngredients(),
                fridge.getEmoticon()
        )).collect(Collectors.toList());
    }
    public List<FridgeResDto> saveCustomerOrder(String token,CustomerSortReq customerSortReq){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        List<Long> fridgeIdOrder = customerSortReq.getFridgeIdOrder();
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < fridgeIdOrder.size(); i++) {
            orderMap.put(fridgeIdOrder.get(i), i + 1); // +1을 해주어 1부터 시작하도록 함
        }
        List<FridgeResDto> fridgeResDtoList = new ArrayList<>();
        for (Long fridgeId : fridgeIdOrder) {
            for (FridgeEntity fridge : fridgeEntities) {
                if (fridge.getFrigeId().equals(fridgeId) && orderMap.containsKey(fridgeId)) {
                    Integer order = orderMap.get(fridgeId);
                    fridge.setCustomerOrder(order.longValue());
                    fridgeRepository.save(fridge);
                    fridgeResDtoList.add(new FridgeResDto(fridgeId, fridge.getIngredients(), fridge.getEmoticon()));
                    break;
                }
            }
        }
        return fridgeResDtoList;
    }
    public List<FridgeResDto> getCustomerFridgeNew(String token){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        Comparator<FridgeEntity> byDate = Comparator.comparing(FridgeEntity::getDate).reversed();
        return fridgeEntities.stream().sorted(byDate).map(fridge -> new FridgeResDto(
                    fridge.getFrigeId(),
                    fridge.getIngredients(),
                    fridge.getEmoticon()
            )).collect(Collectors.toList());
    }
    public List<FridgeResDto> getCustomerFridgeOld(String token){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        Comparator<FridgeEntity> byDate = Comparator.comparing(FridgeEntity::getDate);
        return fridgeEntities.stream().sorted(byDate).map(fridge -> new FridgeResDto(
                    fridge.getFrigeId(),
                    fridge.getIngredients(),
                    fridge.getEmoticon()
        )).collect(Collectors.toList());
    }
    public FridgeResFull updateFridge(String token,FridgeUpReq fridgeUpReq){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(fridgeUpReq.getFridgeId());
        if(opFridge.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        FridgeEntity fridgeEntity=opFridge.get();
        if(fridgeEntity.getCustomer().getCustomerId()!= customerId)
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH);
        fridgeEntity.setIngredients(fridgeUpReq.getIngredients());
        fridgeEntity.setDate(LocalDateTime.now());
        fridgeEntity.setActive(fridgeUpReq.isActive());
        fridgeEntity.setEmoticon(fridgeEntity.getEmoticon());
        return new FridgeResFull(fridgeEntity.getFrigeId(), fridgeEntity.getIngredients(), fridgeEntity.isActive(), fridgeEntity.getEmoticon());
    }
    public FridgeResFull getCustomerFridgeUnique(String token, Long fridgeId){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(fridgeId);
        if(opFridge.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        FridgeEntity fridgeEntity=opFridge.get();
        if(fridgeEntity.getCustomer().getCustomerId()!=customerId)
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH);
        return new FridgeResFull(fridgeEntity.getFrigeId(), fridgeEntity.getIngredients(), fridgeEntity.isActive(), fridgeEntity.getEmoticon());
    }
    public void deleteFridge(String token,DeleteFridgeReq deleteFridgeReq){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(deleteFridgeReq.getFridgeId());
        if(opFridge.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        FridgeEntity deleteFridge=opFridge.get();
        if(deleteFridge.getCustomer().getCustomerId()!= customerId)
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH);
        fridgeRepository.delete(deleteFridge);
    }
}
