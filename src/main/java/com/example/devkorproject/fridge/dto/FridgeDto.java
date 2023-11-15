package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FridgeDto {
    private Long customerId;
    private String ingredients;
}
