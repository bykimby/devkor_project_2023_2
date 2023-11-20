package com.example.devkorproject.fridge.service;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.exception.CustomerDoesNotExistException;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.fridge.dto.*;
import com.example.devkorproject.fridge.entity.FridgeEntity;
import com.example.devkorproject.fridge.exception.FridgeDoesNotExistException;
import com.example.devkorproject.fridge.manager.SessionManager;
import com.example.devkorproject.fridge.repository.FridgeRepository;
import com.example.devkorproject.post.dto.PostRes;
import com.example.devkorproject.post.entity.PhotoEntity;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FridgeService {
    private final FridgeRepository fridgeRepository;
    private final CustomerRepository customerRepository;
    public FridgeService(FridgeRepository fridgeRepository, CustomerRepository customerRepository) {
        this.fridgeRepository = fridgeRepository;
        this.customerRepository = customerRepository;
    }

    public FridgeResFull createFridge(FridgeDto fridgeDto){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(fridgeDto.getCustomerId());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
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
    public List<FridgeResDto> getCustomerFridge(Long customerId){
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST.getMessage());
        Comparator<FridgeEntity> sortOrder= SessionManager.getUserSortOrder(customerId);
        return fridgeEntities.stream().sorted(sortOrder).map(fridgeEntity -> new FridgeResDto(
                fridgeEntity.getFrigeId(),fridgeEntity.getIngredients()
            )).collect(Collectors.toList());
    }
    public void saveUserSortOrder(Long userId, Comparator<FridgeEntity> comparator) {
        SessionManager.setUserSortOrder(userId, comparator);
    }
    public List<FridgeResDto> getCustomerFridgeNew(Long customerId){
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST.getMessage());
        Comparator<FridgeEntity> byDate = Comparator.comparing(FridgeEntity::getDate).reversed();
        return fridgeEntities.stream().sorted(byDate).map(fridge -> new FridgeResDto(
                    fridge.getFrigeId(),
                    fridge.getIngredients()
            )).collect(Collectors.toList());
    }
    public List<FridgeResDto> getCustomerFridgeOld(Long customerId){
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        if(fridgeEntities.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST.getMessage());
        Comparator<FridgeEntity> byDate = Comparator.comparing(FridgeEntity::getDate);
        return fridgeEntities.stream().sorted(byDate).map(fridge -> new FridgeResDto(
                    fridge.getFrigeId(),
                    fridge.getIngredients()
        )).collect(Collectors.toList());
    }
    public FridgeResFull updateFridge(FridgeUpReq fridgeUpReq){
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(fridgeUpReq.getFridgeId());
        if(opFridge.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST.getMessage());
        FridgeEntity fridgeEntity=opFridge.get();
        if(fridgeEntity.getCustomer().getCustomerId()!= fridgeUpReq.getCustomerId())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH.getMessage());
        fridgeEntity.setIngredients(fridgeUpReq.getIngredients());
        fridgeEntity.setDate(LocalDateTime.now());
        fridgeEntity.setActive(fridgeUpReq.isActive());
        fridgeEntity.setEmoticon(fridgeEntity.getEmoticon());
        return new FridgeResFull(fridgeEntity.getFrigeId(), fridgeEntity.getIngredients(), fridgeEntity.isActive(), fridgeEntity.getEmoticon());
    }
    public FridgeResFull getCustomerFridgeUnique(Long customerId, Long fridgeId){
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(fridgeId);
        if(opFridge.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST);
        FridgeEntity fridgeEntity=opFridge.get();
        if(fridgeEntity.getCustomer().getCustomerId()!=customerId)
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH);
        return new FridgeResFull(fridgeEntity.getFrigeId(), fridgeEntity.getIngredients(), fridgeEntity.isActive(), fridgeEntity.getEmoticon());
    }
    public void deleteFridge(DeleteFridgeReq deleteFridgeReq){
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(deleteFridgeReq.getFridgeId());
        if(opFridge.isEmpty())
            throw new GeneralException(ErrorCode.FRIDGE_DOES_NOT_EXIST.getMessage());
        FridgeEntity deleteFridge=opFridge.get();
        if(deleteFridge.getCustomer().getCustomerId()!= deleteFridgeReq.getCustomerId())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH.getMessage());
        fridgeRepository.delete(deleteFridge);
    }
}
