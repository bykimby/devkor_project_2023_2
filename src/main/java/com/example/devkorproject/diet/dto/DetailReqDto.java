package com.example.devkorproject.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class DetailReqDto {
    private String fridge;
    private String keyword;
}
