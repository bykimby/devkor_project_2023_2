package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FridgeUpReq {
    private Long customerId;
    private Long fridgeId;
    private String ingredients;
}
