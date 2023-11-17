package com.example.devkorproject.fridge.service;

import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.exception.CustomerDoesNotExistException;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.fridge.dto.CustomerFridgeReq;
import com.example.devkorproject.fridge.dto.FridgeDto;
import com.example.devkorproject.fridge.dto.FridgeResDto;
import com.example.devkorproject.fridge.dto.FridgeUpReq;
import com.example.devkorproject.fridge.entity.FridgeEntity;
import com.example.devkorproject.fridge.exception.FridgeDoesNotExistException;
import com.example.devkorproject.fridge.repository.FridgeRepository;
import com.example.devkorproject.post.dto.PostRes;
import com.example.devkorproject.post.entity.PhotoEntity;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
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

    public FridgeResDto createFridge(FridgeDto fridgeDto){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(fridgeDto.getCustomerId());
        if(opCustomer.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customer=opCustomer.get();
        FridgeEntity fridgeEntity=FridgeEntity.builder()
                                    .customer(customer)
                                    .date(LocalDate.now())
                                    .ingredients(fridgeDto.getIngredients())
                                    .build();
        fridgeRepository.save(fridgeEntity);
        return new FridgeResDto(fridgeEntity.getFrigeId(),fridgeEntity.getIngredients());
    }
    public List<FridgeResDto> getCustomerFridge(Long customerId){
        List<FridgeEntity> fridgeEntities=fridgeRepository.findByCustomerCustomerId(customerId);
        return fridgeEntities.stream().map(fridge -> {
            return new FridgeResDto(
                    fridge.getFrigeId(),
                    fridge.getIngredients()
            );
        }).collect(Collectors.toList());
    }
    public FridgeResDto updateFridge(FridgeUpReq fridgeUpReq){
        Optional<FridgeEntity> opFridge=fridgeRepository.findById(fridgeUpReq.getFridgeId());
        if(opFridge.isEmpty())
            throw new FridgeDoesNotExistException();
        FridgeEntity fridgeEntity=opFridge.get();
        if(fridgeEntity.getCustomer().getCustomerId()!= fridgeUpReq.getCustomerId())
            throw new CustomerDoesNotMatchException();
        fridgeEntity.setIngredients(fridgeUpReq.getIngredients());
        return new FridgeResDto(fridgeEntity.getFrigeId(), fridgeEntity.getIngredients());
    }
}
