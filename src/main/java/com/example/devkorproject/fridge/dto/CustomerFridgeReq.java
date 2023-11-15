package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
public class CustomerFridgeReq {
    private Long customerId;
}
