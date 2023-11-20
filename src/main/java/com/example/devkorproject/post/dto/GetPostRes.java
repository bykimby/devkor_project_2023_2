package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class GetPostRes {
    private Long postId;
    private String updateTime;
    private Long comments;
    private Long likes;
    private String title;
    private List<byte[]> photo;
    private String type;
    private String customerName;

}
