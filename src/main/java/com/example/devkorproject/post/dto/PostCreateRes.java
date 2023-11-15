package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostCreateRes {
    private LocalDate updateDate;
    private Long comments;
    private Long likes;
    private String title;
    private String body;
    private String category;
    private Long scrap;
    private String type;
}
