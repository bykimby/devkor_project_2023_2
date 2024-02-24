package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FridgeDto {
    private String ingredients;
    private boolean active;
    private String emoticon;
}
