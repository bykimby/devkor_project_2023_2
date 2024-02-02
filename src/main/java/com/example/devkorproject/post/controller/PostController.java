package com.example.devkorproject.post.controller;

import com.example.devkorproject.common.dto.HttpDataResponse;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.service.CustomerService;
import com.example.devkorproject.post.dto.*;
import com.example.devkorproject.post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Transactional
public class PostController {
    private final PostService postService;

//    @PostMapping("")
//    public HttpDataResponse<PostRes> createPost(@RequestBody PostReq postReq){
//        return HttpDataResponse.of(postService.createPost(postReq));
//    }
    @PostMapping("")
    public Long createPhoto(@RequestHeader("Authorization") String authHeader,@RequestPart(value="image", required = false) Optional<List<MultipartFile>> files, @RequestPart(value = "requestDto") PostCreateReqDto postCreateReqDto) throws Exception {
        String token=authHeader.substring(7);
        List<MultipartFile> photos = Collections.emptyList();
        if(files.isPresent()) photos = files.get();
        return postService.create(token,postCreateReqDto, photos);
    }
    @PostMapping("/comment")
    public HttpDataResponse<CommentRes> giveComment(@RequestHeader("Authorization") String authHeader,@RequestBody CommentReq commentReq){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(postService.giveComment(token,commentReq));
    }
    @PostMapping("/likes")
    public HttpDataResponse<LikesRes> giveLikes(@RequestHeader("Authorization") String authHeader,@RequestBody LikesReq likesReq){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(postService.giveLikes(token,likesReq));
    }
    @PostMapping("/scrap")
    public HttpDataResponse<ScrapRes> giveScrap(@RequestHeader("Authorization") String authHeader,@RequestBody ScrapReq scrapReq){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(postService.giveScrap(token,scrapReq));
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
    public HttpDataResponse<List<GetPostRes>> getCustomerPosts(@RequestHeader("Authorization") String authHeader,@RequestParam Long startPostId){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(postService.getCustomerPosts(token,startPostId));
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
    @GetMapping("/scrap")
    public HttpDataResponse<List<GetPostRes>> getScrap(@RequestHeader("Authorization") String authHeader, String type){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(postService.getScrap(token,type));
    }

    @PutMapping("")
    public HttpDataResponse<PostRes> updatePost(@RequestHeader("Authorization") String authHeader,@RequestBody PostUpdateReq postUpdateReq){
        String token=authHeader.substring(7);
        return HttpDataResponse.of(postService.updatePost(token,postUpdateReq));
    }
    @DeleteMapping("")
    public void deletePost(@RequestHeader("Authorization") String authHeader,@RequestBody PostDeleteReq postDeleteReq){
        String token=authHeader.substring(7);
        postService.deletePost(token,postDeleteReq);
    }

    @GetMapping("/weekly")//주간 인기글
    public HttpDataResponse<List<GetPostRes>> weeklyLiked(){
        return HttpDataResponse.of(postService.weeklyLiked());
    }

    @GetMapping("/likes")//인기순 10개씩
    public HttpDataResponse<List<GetPostRes>> getLikedPost(@RequestParam Long startPostId){
        return HttpDataResponse.of(postService.getLikedPost(startPostId));
    }
    @GetMapping("/type/likes")//type별 인기순 10개씩
    public HttpDataResponse<List<GetPostRes>> getLikedPostByType(@RequestParam String type,Long startPostId){
        return HttpDataResponse.of(postService.getLikedPostByType(type,startPostId));
    }



}
