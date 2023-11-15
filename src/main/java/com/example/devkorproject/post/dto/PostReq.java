package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class PostReq {
    private Long customerId;
    private Long comments;
    private Long likes;
    private String title;
    private String body;
    private String category;
    private List<byte[]> photos;
    private Long scrap;
    private String type;
}
