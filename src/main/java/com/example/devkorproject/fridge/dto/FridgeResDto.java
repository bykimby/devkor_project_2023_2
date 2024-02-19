package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FridgeResDto {
    private Long fridgeId;
    private String ingredients;
    private String Emoticon;
}
