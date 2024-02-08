package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FridgeDto {
    private String ingredients;
    private boolean active;
    private String emoticon;
}
