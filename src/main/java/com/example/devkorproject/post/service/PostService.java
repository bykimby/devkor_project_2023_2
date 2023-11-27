package com.example.devkorproject.post.service;

import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.post.dto.*;
import com.example.devkorproject.post.entity.*;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import com.example.devkorproject.post.exception.PostDoesNotExistException;
import com.example.devkorproject.post.repository.CommentRepository;
import com.example.devkorproject.post.repository.LikeRepository;
import com.example.devkorproject.post.repository.PostRepository;
import com.example.devkorproject.post.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    private final CustomerRepository customerRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final LikeRepository likeRepository;

    public PostService(CustomerRepository customerRepository, PostRepository postRepository, CommentRepository commentRepository, ScrapRepository scrapRepository, LikeRepository likeRepository) {
        this.customerRepository = customerRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.scrapRepository = scrapRepository;
        this.likeRepository = likeRepository;
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
            throw new  GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
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
            if(foundPosts.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
        }
        else{
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
            foundPosts = postRepository.findNext20ByTitleContainingOrBodyContainingAndUpdateDateBeforeOrderByUpdateDateDesc(
                        keyword, keyword, startPost.get().getUpdateDate());
            if(foundPosts.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
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
            if(foundPosts.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
        }else{
            Optional<PostEntity> startPost=postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
            foundPosts=postRepository.findTop20ByTypeAndUpdateDateBeforeOrderByUpdateDateDesc(type, startPost.get().getUpdateDate());
            if(foundPosts.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
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
            if(postEntities.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
        } else {
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
            postEntities = postRepository.findTop20ByUpdateDateBeforeOrderByUpdateDateDesc(startPost.get().getUpdateDate());
            if(postEntities.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
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
            if(post.getCustomer().getCustomerName().isEmpty())
                throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
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
            if(postEntities.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
        } else{
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
            postEntities = postRepository.findNext20ByCustomer_CustomerIdAndUpdateDateBeforeOrderByUpdateDateDesc(
                        customerId, startPost.get().getUpdateDate());
            if(postEntities.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
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
    public PostRes getUniquePost(Long postId) {
        try {
            PostEntity foundPost = postRepository.findById(postId).orElseThrow(PostDoesNotExistException::new);
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
        } catch (PostDoesNotExistException ex) {
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST, "Requested post does not exist", ex);
        }
    }
    public PostRes updatePost(PostUpdateReq postUpdateReq){
        Optional<PostEntity> postEntity=postRepository.findById(postUpdateReq.getPostId());
        if(postEntity.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
        if(postEntity.get().getCustomer().getCustomerId()!= postUpdateReq.getCustomerId())
            throw new CustomerDoesNotMatchException();
        Set<PhotoEntity> photos = new HashSet<>();
        for(byte[] photoData:postUpdateReq.getPhotos()){
            PhotoEntity photo=new PhotoEntity();
            photo.setData(photoData);
            photos.add(photo);
        }
        PostEntity foundPost=postEntity.get();
        foundPost.setCustomer(postEntity.get().getCustomer());
        foundPost.setUpdateDate(LocalDateTime.now());
        foundPost.setComments(postUpdateReq.getComments());
        foundPost.setLikes(postUpdateReq.getLikes());
        foundPost.setTitle(postUpdateReq.getTitle());
        foundPost.setBody(postUpdateReq.getBody());
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
            throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST.getMessage());
        PostEntity deletePost=toDeletePost.get();
        if(deletePost.getCustomer().getCustomerId()!= postDeleteReq.getCustomerId())
            throw new  GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH.getMessage());
        postRepository.delete(deletePost);
    }
    public CommentRes giveComment(CommentReq commentReq){
        Optional<PostEntity> opPost=postRepository.findById(commentReq.getPostId());
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity post=opPost.get();
        Optional<CustomerEntity> opCustomer=customerRepository.findById(commentReq.getCustomerId());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        CommentEntity comment=CommentEntity.builder()
                .post(post)
                .customer(customer)
                .contents(commentReq.getContents())
                .time(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        customer.setMyComments(customer.getMyComments()+1);
        post.setComments(post.getComments()+1);
        post.getCommentEntities().add(comment);
        postRepository.save(post);
        return new CommentRes(comment.getPost().getPostId(),comment.getContents(),comment.getCustomer().getCustomerName(),comment.getTime());
    }
    public List<CommentRes> getComments(Long postId){
        Optional<PostEntity> opPost=postRepository.findById(postId);
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity postEntity=opPost.get();
        return postEntity.getCommentEntitiesResponses().stream().toList();
    }
    public List<GetPostRes> weeklyLiked(){
        List<PostEntity> postEntities=postRepository.findTop10ByLikesWithinLastWeek();
        if(postEntities.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
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
        }).collect(Collectors.toList());    }
    public List<GetPostRes> getLikedPost(Long startPostId){
        List<PostEntity> postEntities;
        if(startPostId==0)
        {
            postEntities=postRepository.findTop20ByOrderByLikesDesc();
            if(postEntities.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }
        else
        {
            int pageSize = 20;
            Optional<PostEntity> opPost=postRepository.findById(startPostId);
            if(opPost.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
            PostEntity realPost=opPost.get();
            Long likes=realPost.getLikes();
            postEntities = postRepository.findByLikesLessThanAndPostIdGreaterThanOrderByLikesDescPostIdAsc(
                likes, startPostId, PageRequest.of(0, pageSize)
        );}
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
    public List<GetPostRes> getLikedPostByType(String type,Long startPostId){
        List<PostEntity> postEntities;
        if(startPostId==0)
        {
            postEntities=postRepository.findTop20ByTypeLikesOrderByLikesDesc(type);
            if(postEntities.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }
        else{
            int pageSize = 20;
            Optional<PostEntity> opPost=postRepository.findById(startPostId);
            if(opPost.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
            PostEntity realPost=opPost.get();
            Long likes=realPost.getLikes();
            postEntities = postRepository.findByTypeAndLikesLessThanEqualAndPostIdLessThanOrderByLikesDescUpdateDateAsc(
                    type,likes, startPostId, PageRequest.of(0, pageSize)
            );
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
    public LikesRes giveLikes(LikesReq likesReq){
        Optional<PostEntity> opPost=postRepository.findById(likesReq.getPostId());
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity post=opPost.get();
        Optional<CustomerEntity> opCustomer=customerRepository.findById(likesReq.getCustomerId());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        LikeEntity like=LikeEntity.builder()
                .post(post)
                .customer(customer)
                .build();
        likeRepository.save(like);
        post.setLikes(post.getLikes()+1);
        customer.setMyLikes(customer.getMyLikes()+1);
        return new LikesRes(post.getPostId(), post.getLikes());
    }
    public ScrapRes giveScrap(ScrapReq scrapReq){
        Optional<PostEntity> opPost=postRepository.findById(scrapReq.getPostId());
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity post=opPost.get();
        Optional<CustomerEntity> opCustomer=customerRepository.findById(scrapReq.getCustomerId());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        ScrapEntity scrap=ScrapEntity.builder()
                .post(post)
                .customer(customer)
                .build();
        scrapRepository.save(scrap);
        post.setScrap(post.getScrap()+1);
        return new ScrapRes(post.getPostId(),post.getScrap());
    }
    public List<GetPostRes> getScrap(Long customerId, String type){
        List<ScrapEntity> scrapEntities=scrapRepository.findByCustomer_CustomerId(customerId);
        if(scrapEntities.isEmpty())
            throw new GeneralException(ErrorCode.SCRAP_DOES_NOT_EXIST);
        return scrapEntities.stream()
                .filter(scrap -> scrap.getPost().getType().equals(type)).map(scrap -> {
            PostEntity post=scrap.getPost();
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
}
