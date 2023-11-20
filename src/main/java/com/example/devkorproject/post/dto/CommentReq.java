package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentReq {
    private Long postId;
    private Long customerId;
    private String contents;

}
