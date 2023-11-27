package com.example.devkorproject.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class FridgeDietReqDto {
    private String dietName;
    private String time;
    private String difficulty;
    private String type;
    private String fridge;
    private String keyword;
}
