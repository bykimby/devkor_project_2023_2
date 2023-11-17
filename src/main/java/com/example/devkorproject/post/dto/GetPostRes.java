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
    //TODO : photo 관련 설정
    private String type;
    private String customerName;

}