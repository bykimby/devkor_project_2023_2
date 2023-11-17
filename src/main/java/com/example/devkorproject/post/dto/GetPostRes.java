package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetPostRes {
    private Long postId;
    private LocalDateTime updateTime;
    private Long comments;
    private Long likes;
    private String title;
    //TODO : photo 관련 설정
    private String type;
    private String customerName;

}
