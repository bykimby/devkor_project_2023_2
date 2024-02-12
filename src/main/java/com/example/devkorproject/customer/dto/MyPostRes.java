package com.example.devkorproject.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyPostRes {
    private Long postId;
    private String title;
    private LocalDateTime updateTime;
}
