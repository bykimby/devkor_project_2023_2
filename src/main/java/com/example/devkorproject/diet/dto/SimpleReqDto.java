package com.example.devkorproject.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Getter
@Setter
public class SimpleReqDto implements Serializable {
    private String type;
    private String fridge;
    private String keyword;
}
