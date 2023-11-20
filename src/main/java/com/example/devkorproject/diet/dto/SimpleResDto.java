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
public class SimpleResDto implements Serializable {
    private String dietName;
    private String description;
    private String time;
    private String difficulty;
}
