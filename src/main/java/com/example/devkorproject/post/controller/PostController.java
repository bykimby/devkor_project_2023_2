package com.example.devkorproject.post.controller;

import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.post.dto.*;
import com.example.devkorproject.post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    @PostMapping("/comment")
    public HttpDataResponse<CommentRes> giveComment(@RequestBody CommentReq commentReq){
        return HttpDataResponse.of(postService.giveComment(commentReq));
    }
    @GetMapping("/unique")
    public HttpDataResponse<PostRes> getUniquePost(@RequestParam Long postId){
        return HttpDataResponse.of(postService.getUniquePost(postId));
    }
    @GetMapping("")
    public HttpDataResponse<List<GetPostRes>> getAllPosts(@RequestParam Long startPostId){
        return HttpDataResponse.of(postService.getAllPosts(startPostId));
    }

    @GetMapping("/customer")
    public HttpDataResponse<List<GetPostRes>> getCustomerPosts(@RequestHeader("customerId") Long customerId,@RequestParam Long startPostId){
        return HttpDataResponse.of(postService.getCustomerPosts(customerId,startPostId));
    }

    @GetMapping("/keyword")
    public HttpDataResponse<List<GetPostRes>> keywordSearchPost(@RequestParam String keyword,Long startPostId){
        return HttpDataResponse.of(postService.keywordSearchPost(keyword,startPostId));
    }

    @GetMapping("/type")
    public HttpDataResponse<List<GetPostRes>> typeSearchPost(@RequestParam String type,Long startPostId) {
        return HttpDataResponse.of(postService.typeSearchPost(type,startPostId));
    }
    @GetMapping("/comment")
    public HttpDataResponse<List<CommentRes>> getComments(@RequestParam Long postId){
        return HttpDataResponse.of(postService.getComments(postId));
    }

    @PutMapping("")
    public HttpDataResponse<PostRes> updatePost(@RequestBody PostUpdateReq postUpdateReq){
        return HttpDataResponse.of(postService.updatePost(postUpdateReq));
    }
    @DeleteMapping("")
    public void deletePost(@RequestBody PostDeleteReq postDeleteReq){
        postService.deletePost(postDeleteReq);
    }

    @GetMapping("/sort")
    public HttpDataResponse<List<PostOrderRes>> orderPost(@RequestBody PostOrderReq postOrderReq) {
        return HttpDataResponse.of(postService.orderPost(postOrderReq));
    }
}
