package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostOrderRes {
    private String customerName;
    private LocalDateTime updateDate;
    private Long comments;
    private Long likes;
    private String title;
}
