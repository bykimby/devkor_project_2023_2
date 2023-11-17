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

import java.time.LocalDateTime;
import java.util.*;
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
                .updateDate(LocalDateTime.now())
                .comments(postReq.getComments())
                .likes(postReq.getLikes())
                .title(postReq.getTitle())
                .body(postReq.getBody())
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
                postEntity.getScrap(),
                postEntity.getType()
        );
    }
    public List<GetPostRes> keywordSearchPost(String keyword,Long startPostId){
        List<PostEntity> foundPosts;
        if(startPostId==0){
            foundPosts=postRepository.findTop20ByTitleContainingOrBodyContainingOrderByUpdateDateDesc(keyword,keyword);
        }
        else{
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new PostDoesNotExistException();
            foundPosts = postRepository.findNext20ByTitleContainingOrBodyContainingAndUpdateDateBeforeOrderByUpdateDateDesc(
                        keyword, keyword, startPost.get().getUpdateDate());
        }
        return foundPosts.stream().map(post -> {
            byte[] firstPhotoData = post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<byte[]> photo = new ArrayList<>();
            if (firstPhotoData != null) {
                photo.add(firstPhotoData);
            }
            return new GetPostRes(
                post.getPostId(),
                post.getUpdateDate().toString(),
                post.getComments(),
                post.getLikes(),
                post.getTitle(),
                photo,
                post.getType(),
                post.getCustomer().getCustomerName()
            );
        }).collect(Collectors.toList());
    }
    public List<GetPostRes> typeSearchPost(String type,Long startPostId){
        List<PostEntity> foundPosts;
        if(startPostId==0){
            foundPosts = postRepository.findTop20ByTypeOrderByUpdateDateDesc(type);
        }else{
            Optional<PostEntity> startPost=postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new PostDoesNotExistException();
            foundPosts=postRepository.findTop20ByTypeAndUpdateDateBeforeOrderByUpdateDateDesc(type, startPost.get().getUpdateDate());
        }
        return foundPosts.stream().map(post -> {
            byte[] firstPhotoData = post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<byte[]> photo = new ArrayList<>();
            if (firstPhotoData != null) {
                photo.add(firstPhotoData);
            }
            return new GetPostRes(
                    post.getPostId(),
                    post.getUpdateDate().toString(),
                    post.getComments(),
                    post.getLikes(),
                    post.getTitle(),
                    photo,
                    post.getType(),
                    post.getCustomer().getCustomerName()
            );
        }).collect(Collectors.toList());
    }
    public List<GetPostRes> getAllPosts(Long startPostId){
        List<PostEntity> postEntities;
        if (startPostId == 0) {
            postEntities = postRepository.findTop20ByOrderByUpdateDateDesc();
        } else {
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new PostDoesNotExistException();
            postEntities = postRepository.findTop20ByUpdateDateBeforeOrderByUpdateDateDesc(startPost.get().getUpdateDate());
        }
        return postEntities.stream().map(post -> {
            byte[] firstPhotoData = post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<byte[]> photo = new ArrayList<>();
            if (firstPhotoData != null) {
                photo.add(firstPhotoData);
            }
            return new GetPostRes(
                    post.getPostId(),
                    post.getUpdateDate().toString(),
                    post.getComments(),
                    post.getLikes(),
                    post.getTitle(),
                    photo,
                    post.getType(),
                    post.getCustomer().getCustomerName()
            );
        }).collect(Collectors.toList());
    }
    public List<GetPostRes> getCustomerPosts(Long customerId,Long startPostId) {
        List<PostEntity> postEntities;
        if(startPostId==0){
            postEntities = postRepository.findTop20ByCustomer_CustomerIdOrderByUpdateDateDesc(customerId);
        } else{
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new PostDoesNotExistException();
            postEntities = postRepository.findNext20ByCustomer_CustomerIdAndUpdateDateBeforeOrderByUpdateDateDesc(
                        customerId, startPost.get().getUpdateDate());
        }
        return postEntities.stream().map(post -> {
            byte[] firstPhotoData = post.getPhotos().stream()
                    .map(PhotoEntity::getData)
                    .findFirst()
                    .orElse(null);

            List<byte[]> photos = new ArrayList<>();
            if (firstPhotoData != null) {
                photos.add(firstPhotoData);
            }
            return new GetPostRes(
                    post.getPostId(),
                    post.getUpdateDate().toString(),
                    post.getComments(),
                    post.getLikes(),
                    post.getTitle(),
                    photos,
                    post.getType(),
                    post.getCustomer().getCustomerName()
            );
        }).collect(Collectors.toList());
    }
    public PostRes getUniquePost(Long postId){
        Optional<PostEntity> opfoundPost=postRepository.findById(postId);
        if(opfoundPost.isEmpty())
            throw new PostDoesNotExistException();
        PostEntity foundPost=opfoundPost.get();
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
                foundPost.getScrap(),
                foundPost.getType()
        );
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
        //foundPost.setUpdateDate(LocalDateTime.now());
        foundPost.setComments(postUpdateReq.getComments());
        foundPost.setLikes(postUpdateReq.getLikes());
        foundPost.setTitle(postUpdateReq.getTitle());
        foundPost.setBody(postUpdateReq.getBody());
        //foundPost.setCategory(postUpdateReq.getCategory());
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
