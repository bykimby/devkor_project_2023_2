package com.example.devkorproject.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class DietResDto {
    private String dietName;
    private String description;
    private String ingredients;
    private String recipe;
    private String time;
    private String difficulty;
}
