package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteFridgeReq {
    private Long fridgeId;
    private Long customerId;
}
