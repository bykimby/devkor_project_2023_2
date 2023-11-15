package com.example.devkorproject.post.controller;

import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.post.dto.*;
import com.example.devkorproject.post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Transactional
public class PostController {
    private final PostService postService;
    @PostMapping("")
    public HttpDataResponse<PostRes> createPost(@RequestBody PostReq postReq){
        return HttpDataResponse.of(postService.createPost(postReq));
    }
    @GetMapping("")
    public HttpDataResponse<List<PostRes>> getAllPosts(){
        return HttpDataResponse.of(postService.getAllPosts());
    }

    @GetMapping("/customer")
    public HttpDataResponse<List<PostRes>> getCustomerPosts(@RequestHeader("customerId") Long customerId){
        return HttpDataResponse.of(postService.getCustomerPosts(customerId));
    }

    @GetMapping("/keyword")
    public HttpDataResponse<List<PostRes>> keywordSearchPost(@RequestParam String keyword){
        return HttpDataResponse.of(postService.keywordSearchPost(keyword));
    }

    @GetMapping("/type")
    public HttpDataResponse<List<PostRes>> typeSearchPost(@RequestParam String type) {
        return HttpDataResponse.of(postService.typeSearchPost(type));
    }

    @PutMapping("")
    public HttpDataResponse<PostRes> updatePost(@RequestBody PostUpdateReq postUpdateReq){
        return HttpDataResponse.of(postService.updatePost(postUpdateReq));
    }
    @DeleteMapping("")
    public void deletePost(@RequestBody PostDeleteReq postDeleteReq){
        postService.deletePost(postDeleteReq);
    }
}
