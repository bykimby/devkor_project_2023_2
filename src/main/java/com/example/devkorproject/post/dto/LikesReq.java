package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikesReq {
    private Long customerId;
    private Long postId;
}
