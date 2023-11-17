package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetPostReq {
    private String type;//식단 자유 all
    private Long startPostId;
}
