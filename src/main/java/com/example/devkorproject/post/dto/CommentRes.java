package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRes {
    private Long postId;
    private String contents;
    private String customerName;
    private LocalDateTime time;
}
