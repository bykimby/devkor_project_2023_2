package com.example.devkorproject.post.service;

import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.exception.CustomerDoesNotExistException;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.post.dto.*;
import com.example.devkorproject.post.entity.PhotoEntity;
import com.example.devkorproject.post.entity.PostEntity;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import com.example.devkorproject.post.exception.PostDoesNotExistException;
import com.example.devkorproject.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    private final CustomerRepository customerRepository;
    private final PostRepository postRepository;

    public PostService(CustomerRepository customerRepository, PostRepository postRepository) {
        this.customerRepository = customerRepository;
        this.postRepository = postRepository;
    }

    public PostRes createPost(PostReq postReq){//photo는 따로 요청
        Set<PhotoEntity> photos = new HashSet<>();
        for(byte[] photoData:postReq.getPhotos()){
            PhotoEntity photo=new PhotoEntity();
            photo.setData(photoData);
            photos.add(photo);
        }
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(postReq.getCustomerId());
        if(opCustomer.isEmpty())
            throw new CustomerDoesNotExistException();
        CustomerEntity customer=opCustomer.get();
        PostEntity postEntity=PostEntity.builder()
                .updateDate(LocalDate.now())
                .comments(postReq.getComments())
                .likes(postReq.getLikes())
                .title(postReq.getTitle())
                .body(postReq.getBody())
                .category(postReq.getCategory())
                .photos(photos)
                .scrap(postReq.getScrap())
                .type(postReq.getType())
                .customer(customer)
                .build();
        for(PhotoEntity photo:photos){
            photo.setPost(postEntity);
        }
        List<byte[]> photosByte = postEntity.getPhotos().stream()
                .map(PhotoEntity::getData)
                .collect(Collectors.toList());
        postRepository.save(postEntity);
        return new PostRes(
                postEntity.getPostId(),
                postEntity.getUpdateDate(),
                postEntity.getComments(),
                postEntity.getLikes(),
                postEntity.getTitle(),
                postEntity.getBody(),
                photosByte,
                postEntity.getCategory(),
                postEntity.getScrap(),
                postEntity.getType()
        );
    }
    public List<PostRes> keywordSearchPost(String keyword){
        List<PostEntity> foundPosts=postRepository.findByTitleContainingOrBodyContaining(keyword,keyword);
        if(foundPosts.isEmpty())
            throw new PostDoesNotExistException();
        return foundPosts.stream().map(post -> {
            List<byte[]> photos=post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .collect(Collectors.toList());
            return new PostRes(
                post.getPostId(),
                post.getUpdateDate(),
                post.getComments(),
                post.getLikes(),
                post.getTitle(),
                post.getBody(),
                photos,
                post.getCategory(),
                post.getScrap(),
                post.getType()
            );
        }).collect(Collectors.toList());
    }
    public List<PostRes> typeSearchPost(String type){
        List<PostEntity> foundPosts=postRepository.findByCategory(type);
        if(foundPosts.isEmpty())
            throw new PostDoesNotExistException();
        return foundPosts.stream().map(post -> {
            List<byte[]> photos=post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .collect(Collectors.toList());
            return new PostRes(
                    post.getPostId(),
                    post.getUpdateDate(),
                    post.getComments(),
                    post.getLikes(),
                    post.getTitle(),
                    post.getBody(),
                    photos,
                    post.getCategory(),
                    post.getScrap(),
                    post.getType()
            );
        }).collect(Collectors.toList());
    }
    public List<PostRes> getAllPosts(){
        List<PostEntity> postEntities=postRepository.findAll();
        return postEntities.stream().map(post -> {
            List<byte[]> photos=post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .collect(Collectors.toList());
            return new PostRes(
                    post.getPostId(),
                    post.getUpdateDate(),
                    post.getComments(),
                    post.getLikes(),
                    post.getTitle(),
                    post.getBody(),
                    photos,
                    post.getCategory(),
                    post.getScrap(),
                    post.getType()
            );
        }).collect(Collectors.toList());
    }
    public List<PostRes> getCustomerPosts(Long customerId) {
        List<PostEntity> postEntities = postRepository.findByCustomer_CustomerId(customerId);
        if(postEntities.isEmpty())
            throw new PostDoesNotExistException();
        return postEntities.stream().map(post -> {
            List<byte[]> photos=post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .collect(Collectors.toList());
            return new PostRes(
                    post.getPostId(),
                    post.getUpdateDate(),
                    post.getComments(),
                    post.getLikes(),
                    post.getTitle(),
                    post.getBody(),
                    photos,
                    post.getCategory(),
                    post.getScrap(),
                    post.getType()
            );
        }).collect(Collectors.toList());
    }
    public PostRes updatePost(PostUpdateReq postUpdateReq){
        Optional<PostEntity> postEntity=postRepository.findById(postUpdateReq.getPostId());
        if(postEntity.isEmpty())
            throw new PostDoesNotExistException();
        if(postEntity.get().getCustomer().getCustomerId()!= postUpdateReq.getCustomerId())
            throw new CustomerDoesNotMatchException();
        Set<PhotoEntity> photos = new HashSet<>();
        for(byte[] photoData:postUpdateReq.getPhotos()){
            PhotoEntity photo=new PhotoEntity();
            photo.setData(photoData);
            photos.add(photo);
        }
        PostEntity foundPost=postEntity.get();
        foundPost.setUpdateDate(LocalDate.now());
        foundPost.setComments(postUpdateReq.getComments());
        foundPost.setLikes(postUpdateReq.getLikes());
        foundPost.setTitle(postUpdateReq.getTitle());
        foundPost.setBody(postUpdateReq.getBody());
        foundPost.setCategory(postUpdateReq.getCategory());
        foundPost.setPhotos(photos);
        foundPost.setType(postUpdateReq.getType());
        foundPost.setScrap(postUpdateReq.getScrap());
        List<byte[]> photosByte = foundPost.getPhotos().stream()
                .map(PhotoEntity::getData)
                .collect(Collectors.toList());
        return new PostRes(
                foundPost.getPostId(),
                foundPost.getUpdateDate(),
                foundPost.getComments(),
                foundPost.getLikes(),
                foundPost.getTitle(),
                foundPost.getBody(),
                photosByte,
                foundPost.getCategory(),
                foundPost.getScrap(),
                foundPost.getType()
        );
    }
    public void deletePost(PostDeleteReq postDeleteReq){
        Optional<PostEntity> toDeletePost=postRepository.findById(postDeleteReq.getPostId());
        if(toDeletePost.isEmpty())
            throw new PostDoesNotExistException();
        PostEntity deletePost=toDeletePost.get();
        if(deletePost.getCustomer().getCustomerId()!= postDeleteReq.getCustomerId())
            throw new CustomerDoesNotMatchException();
        if(deletePost.getCustomer().getCustomerId()!= postDeleteReq.getCustomerId())
            throw new CustomerDoesNotMatchException();
        postRepository.delete(deletePost);
    }
}
