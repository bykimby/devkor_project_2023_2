package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostOrderReq {
    private int pageNo;
    private String criteria;
    private String sort;
}
