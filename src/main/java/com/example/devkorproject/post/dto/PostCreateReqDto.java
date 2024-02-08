package com.example.devkorproject.post.dto;

import com.example.devkorproject.customer.entity.CustomerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostCreateReqDto {

    private String title;
    private String body;
    private String type;
}
