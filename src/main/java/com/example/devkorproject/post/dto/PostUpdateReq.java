package com.example.devkorproject.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class PostUpdateReq {
    private Long postId;
    private Long customerId;
    private Long comments;
    private Long likes;
    private String title;
    private String body;
//    private List<byte[]> photos;//TODO Photo를 list로 짜야 함
    private List<String> filePaths;
    private Long scrap;
    private String type;
}
