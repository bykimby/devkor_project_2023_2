package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FridgeResFull {
    private Long fridgeId;
    private String ingredients;
    private boolean active;
    private String emoticon;
}
