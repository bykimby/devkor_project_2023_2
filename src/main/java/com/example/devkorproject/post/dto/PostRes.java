package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class PostRes {
    private Long postId;
    private LocalDate updateDate;
    private Long comments;
    private Long likes;
    private String title;
    private String body;
    private List<byte[]> photos;
    private String category;
    private Long scrap;
    private String type;
}
