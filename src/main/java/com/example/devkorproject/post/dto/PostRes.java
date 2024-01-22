package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PostRes {
    private Long postId;
    private LocalDateTime updateDate;
    private Long comments;
    private Long likes;
    private String title;
    private String body;
//    private List<byte[]> photos;
    private List<String> filePaths;
    private Long scrap;
    private String type;
}
