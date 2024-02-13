package com.example.devkorproject.post.service;


import com.example.devkorproject.alarm.service.FCMService;
import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;
import com.example.devkorproject.post.dto.*;
import com.example.devkorproject.post.entity.*;
import com.example.devkorproject.post.exception.CustomerDoesNotMatchException;
import com.example.devkorproject.post.exception.PostDoesNotExistException;
import com.example.devkorproject.post.repository.CommentRepository;
import com.example.devkorproject.post.repository.PhotoRepository;
import com.example.devkorproject.post.repository.LikeRepository;
import com.example.devkorproject.post.repository.PostRepository;
import com.example.devkorproject.post.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class PostService {
    private final CustomerRepository customerRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final LikeRepository likeRepository;

    private final PhotoRepository photoRepository;
    private final JwtUtil jwtUtil;

    private final FCMService fcmService;


    public PostService(CustomerRepository customerRepository, PostRepository postRepository, CommentRepository commentRepository, ScrapRepository scrapRepository, LikeRepository likeRepository, PhotoRepository photoRepository, JwtUtil jwtUtil, FCMService fcmService) {
        this.customerRepository = customerRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.scrapRepository = scrapRepository;
        this.likeRepository = likeRepository;
        this.photoRepository = photoRepository;
        this.jwtUtil = jwtUtil;
        this.fcmService =  fcmService;
    }

//    public PostRes createPost(PostReq postReq){//photo는 따로 요청
//        List<PhotoEntity> photos = Collections.emptyList();
//        for(byte[] photoData:postReq.getPhotos()){
//            PhotoEntity photo=new PhotoEntity();
//            photo.setData(photoData);
//            photos.add(photo);
//        }
//        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(postReq.getCustomerId());
//        if(opCustomer.isEmpty())
//            throw new  GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
//        CustomerEntity customer=opCustomer.get();
//        PostEntity postEntity=PostEntity.builder()
//                .updateDate(LocalDateTime.now())
//                .comments(postReq.getComments())
//                .likes(postReq.getLikes())
//                .title(postReq.getTitle())
//                .body(postReq.getBody())
//                .photo(photos)
//                .scrap(postReq.getScrap())
//                .type(postReq.getType())
//                .customer(customer)
//                .build();
//        for(PhotoEntity photo:photos){
//            photo.setPost(postEntity);
//        }
//        List<byte[]> photosByte = postEntity.getPhoto().stream()
//                .map(PhotoEntity::getData)
//                .collect(Collectors.toList());
//        postRepository.save(postEntity);
//        return new PostRes(
//                postEntity.getPostId(),
//                postEntity.getUpdateDate(),
//                postEntity.getComments(),
//                postEntity.getLikes(),
//                postEntity.getTitle(),
//                postEntity.getBody(),
//                photosByte,
//                postEntity.getScrap(),
//                postEntity.getType()
//        );
//    }
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
            String firstPhotoData = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<String> photo = new ArrayList<>();
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
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }else{
            Optional<PostEntity> startPost=postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
            foundPosts=postRepository.findTop20ByTypeAndUpdateDateBeforeOrderByUpdateDateDesc(type, startPost.get().getUpdateDate());
            if(foundPosts.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }
        return foundPosts.stream().map(post -> {
            String firstPhotoPath = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<String> photo = new ArrayList<>();
            if (firstPhotoPath != null) {
                photo.add(firstPhotoPath);
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
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        } else {
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
            postEntities = postRepository.findTop20ByUpdateDateBeforeOrderByUpdateDateDesc(startPost.get().getUpdateDate());
            if(postEntities.isEmpty())
                throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }
        return postEntities.stream().map(post -> {
            String firstPhotoData = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<String> photo = new ArrayList<>();
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
    public List<GetPostRes> getCustomerPosts(String token,Long startPostId) {
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<PostEntity> postEntities;
        if(startPostId==0){
            postEntities = postRepository.findTop20ByCustomer_CustomerIdOrderByUpdateDateDesc(customerId);
            if(postEntities.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        } else{
            Optional<PostEntity> startPost = postRepository.findById(startPostId);
            if(startPost.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
            postEntities = postRepository.findNext20ByCustomer_CustomerIdAndUpdateDateBeforeOrderByUpdateDateDesc(
                        customerId, startPost.get().getUpdateDate());
            if(postEntities.isEmpty())
                throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }
        return postEntities.stream().map(post -> {
            String firstPhotoPath = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst()
                    .orElse(null);

            List<String> photos = new ArrayList<>();
            if (firstPhotoPath != null) {
                photos.add(firstPhotoPath);
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
            List<String> photoPaths = foundPost.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .collect(Collectors.toList());
            return new PostRes(
                    foundPost.getPostId(),
                    foundPost.getUpdateDate(),
                    foundPost.getComments(),
                    foundPost.getLikes(),
                    foundPost.getTitle(),
                    foundPost.getBody(),
                    photoPaths,
                    foundPost.getScrap(),
                    foundPost.getType()
            );
        } catch (PostDoesNotExistException ex) {
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        }
    }
    public PostRes updatePost(String token,PostUpdateReq postUpdateReq){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<PostEntity> postEntity=postRepository.findById(postUpdateReq.getPostId());
        if(postEntity.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        if(postEntity.get().getCustomer().getCustomerId()!= customerId)
            throw new CustomerDoesNotMatchException();
        List<PhotoEntity> photos = Collections.emptyList();
        for(String filePath:postUpdateReq.getFilePaths()){
            PhotoEntity photo=new PhotoEntity();
            photo.setFilePath(filePath);
            photos.add(photo);
        }
        PostEntity foundPost=postEntity.get();
        foundPost.setCustomer(postEntity.get().getCustomer());
        foundPost.setUpdateDate(LocalDateTime.now());
        foundPost.setComments(postUpdateReq.getComments());
        foundPost.setLikes(postUpdateReq.getLikes());
        foundPost.setTitle(postUpdateReq.getTitle());
        foundPost.setBody(postUpdateReq.getBody());
        foundPost.setPhoto(photos);
        foundPost.setType(postUpdateReq.getType());
        foundPost.setScrap(postUpdateReq.getScrap());
        List<String> photoPaths = foundPost.getPhoto().stream()
                .map(PhotoEntity::getFilePath)
                .collect(Collectors.toList());
        return new PostRes(
                foundPost.getPostId(),
                foundPost.getUpdateDate(),
                foundPost.getComments(),
                foundPost.getLikes(),
                foundPost.getTitle(),
                foundPost.getBody(),
                photoPaths,
                foundPost.getScrap(),
                foundPost.getType()
        );
    }
    public void deletePost(String token,PostDeleteReq postDeleteReq){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<PostEntity> toDeletePost=postRepository.findById(postDeleteReq.getPostId());
        if(toDeletePost.isEmpty())
            throw new  GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity deletePost=toDeletePost.get();
        if(deletePost.getCustomer().getCustomerId()!= customerId)
            throw new  GeneralException(ErrorCode.CUSTOMER_DOES_NOT_MATCH);
        postRepository.delete(deletePost);
    }

    public CommentRes giveComment(String token,CommentReq commentReq) throws IOException {
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<PostEntity> opPost=postRepository.findById(commentReq.getPostId());
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity post=opPost.get();
        Optional<CustomerEntity> opCustomer=customerRepository.findById(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();

        String targetToken = searchFCMTokenByPostId(commentReq.getPostId());
        String postTitle = post.getTitle();
        String customerName = customer.getCustomerName();

        String message = customerName + "님이 " +
                postTitle + " 글에 댓글을 달았습니다.";

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

        fcmService.sendMessageTo(targetToken, "BabyMeal", message);

        return new CommentRes(comment.getPost().getPostId(),comment.getContents(),comment.getCustomer().getCustomerName(),comment.getTime());
    }

    public String searchFCMTokenByPostId(Long postId){
        Optional<String> opFCMToken = postRepository.findCustomerFcmTokenByPostId(postId);
        if(opFCMToken.isEmpty())
            throw new GeneralException(ErrorCode.FCMTOKEN_DOES_NOT_EXIST);
        return opFCMToken.get();
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
            String firstPhotoPath = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst()
                    .orElse(null);

            List<String> photos = new ArrayList<>();
            if (firstPhotoPath != null) {
                photos.add(firstPhotoPath);
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
            String firstPhotoData = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst()
                    .orElse(null);

            List<String> photos = new ArrayList<>();
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
            String firstPhotoData = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst()
                    .orElse(null);

            List<String> photos = new ArrayList<>();
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



    public LikesRes giveLikes(String token,LikesReq likesReq)throws IOException{
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);

        Optional<PostEntity> opPost=postRepository.findById(likesReq.getPostId());
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity post=opPost.get();
        Optional<CustomerEntity> opCustomer=customerRepository.findById(post.getCustomer().getCustomerId());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);

        CustomerEntity giveCustomer=opCustomer.get();
        CustomerEntity getCustomer=customerRepository.findById(customerId).get();

        CustomerEntity customer=opCustomer.get();

        String targetToken = searchFCMTokenByPostId(likesReq.getPostId());
        String postTitle = post.getTitle();
        String customerName = customer.getCustomerName();

        String message = customerName + "님이 " +
                postTitle + " 글에 찜을 눌렀습니다.";


        LikeEntity like=LikeEntity.builder()
                .post(post)
                .customer(giveCustomer)
                .build();
        likeRepository.save(like);
        post.setLikes(post.getLikes()+1);

        getCustomer.setMyLikes(getCustomer.getMyLikes()+1);

        customer.setMyLikes(customer.getMyLikes()+1);

        fcmService.sendMessageTo(targetToken, "BabyMeal", message);


        return new LikesRes(post.getPostId(), post.getLikes());
    }
    public ScrapRes giveScrap(String token,ScrapReq scrapReq){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<PostEntity> opPost=postRepository.findById(scrapReq.getPostId());
        if(opPost.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        PostEntity post=opPost.get();
        Optional<CustomerEntity> opCustomer=customerRepository.findById(customerId);
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
    public List<GetPostRes> getScrap(String token, String type){
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        List<ScrapEntity> scrapEntities=scrapRepository.findByCustomer_CustomerId(customerId);
        if(scrapEntities.isEmpty())
            throw new GeneralException(ErrorCode.SCRAP_DOES_NOT_EXIST);
        return scrapEntities.stream()
                .filter(scrap -> scrap.getPost().getType().equals(type)).map(scrap -> {
            PostEntity post=scrap.getPost();
            String firstPhotoData = post.getPhoto().stream()
                    .map(PhotoEntity::getFilePath)
                    .findFirst() // 첫 번째 사진 데이터만 가져옵니다.
                    .orElse(null); // 사진이 없을 경우 null 반환

            List<String> photo = new ArrayList<>();
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

    public Long create(String token,PostCreateReqDto requestDto, List<MultipartFile> files) throws Exception{
        if(!jwtUtil.validateToken(token))
            throw new GeneralException(ErrorCode.WRONG_TOKEN);
        Long customerId= jwtUtil.getCustomerIdFromToken(token);
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer = opCustomer.get();

        PostEntity post = PostEntity.builder()
                .updateDate(LocalDateTime.now())
                .comments(Long.valueOf(0))
                .likes(Long.valueOf(0))
                .title(requestDto.getTitle())
                .body(requestDto.getBody())
                .scrap(Long.valueOf(0))
                .type(requestDto.getType())
                .customer(customer)
                .build();

        int check = 1;
        for (MultipartFile image : files) {
            if (image.isEmpty()) check = 0;
        }

        if(check == 1) {
            List<PhotoEntity> fileList = new ArrayList<>();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String current_date = now.format(dateTimeFormatter);

            String absolutePath = new File("").getAbsolutePath() + File.separator;

            String path = "images" + File.separator + current_date;
            File file = new File(path);

            if(!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                if (!wasSuccessful) {
                    System.out.println("file: was not successful");
                }
            }

            for(MultipartFile multipartFile : files){

                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                if(ObjectUtils.isEmpty(contentType)){
                    break;
                } else {
                    if (contentType.contains("image/jpeg")) {
                        originalFileExtension = ".jpg";
                    } else if (contentType.contains("image/png")) {
                        originalFileExtension = ".png";
                    } else {
                        break;
                    }
                }

                UUID uuid = UUID.randomUUID();
                String newFileName = uuid + originalFileExtension;

                PhotoEntity photo = PhotoEntity.builder()
                        .origFileName(multipartFile.getOriginalFilename())
                        .filePath(path + File.separator + newFileName)
                        .fileSize(multipartFile.getSize())
                        .build();

                fileList.add(photo);

                file = new File(absolutePath + path + File.separator + newFileName);
                multipartFile.transferTo(file);

                file.setWritable(true);
                file.setReadable(true);

            }

            for(PhotoEntity photo : fileList){
                photo.setPost(post);
                photoRepository.save(photo);
            }

        }

        return postRepository.save(post).getPostId();
    }
}
