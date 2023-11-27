package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikesRes {
    private Long postId;
    private Long likes;
}
