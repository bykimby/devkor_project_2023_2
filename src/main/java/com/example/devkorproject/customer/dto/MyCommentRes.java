package com.example.devkorproject.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyCommentRes {
    private Long postId;
    private Long commentId;
    private String title;
    private String contents;
    private LocalDateTime time;

}
