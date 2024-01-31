package com.example.devkorproject.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class RegisterRes {
    private String accessToken;
}
