package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PostDeleteReq {
    private Long postId;
}
