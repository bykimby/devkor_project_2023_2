package com.example.devkorproject.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class FridgeSimpleDietResDto {
    private Long simpleDietId;
    private String dietName;
    private Long time;
    private String difficulty;
    private boolean heart;
}
