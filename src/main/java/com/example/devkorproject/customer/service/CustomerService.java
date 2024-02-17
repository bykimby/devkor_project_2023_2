package com.example.devkorproject.customer.service;

import com.example.devkorproject.auth.jwt.JwtUtil;
import com.example.devkorproject.common.constants.ErrorCode;
import com.example.devkorproject.common.exception.GeneralException;
import com.example.devkorproject.customer.dto.*;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.customer.repository.CustomerRepository;

import com.example.devkorproject.post.entity.CommentEntity;
import com.example.devkorproject.post.entity.PostEntity;
import com.example.devkorproject.post.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public CustomerService(CustomerRepository customerRepository, CommentRepository commentRepository, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
    }

    public CustomerEntity searchByMemberId(Long customerId) {
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
        CustomerEntity customer = opCustomer.get();
        return customer;
    }


    public void saveFCMToken(Long customerId, String fcmToken){
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());
        CustomerEntity customer = opCustomer.get();
        customer.setFcmToken(fcmToken);
    }

    public String searchFCMTokenByCustomerId(Long customerId) {
        Optional<CustomerEntity> opCustomer = customerRepository.findCustomerEntityByCustomerId(customerId);
        if (opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST.getMessage());

        Optional<String> opFcmToken = customerRepository.findFcmTokenByCustomerId(customerId);
        if (opFcmToken.isEmpty())
            throw new GeneralException(ErrorCode.FCMTOKEN_DOES_NOT_EXIST.getMessage());

        return opFcmToken.get();
    }

    public LoginRes login(LoginReq loginReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByEmail(loginReq.getEmail());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerEntity customer = opCustomer.get();
        if (!encoder.matches(loginReq.getPassword(), customer.getPassword()))
            throw new GeneralException(ErrorCode.WRONG_PASSWORD);
        String accessToken= jwtUtil.createToken(customer.getCustomerId());
        if((customer.getMyPosts()>=50)&&(customer.getMyComments()>=50)&&(customer.getMyLikes()>=50))
            customer.setRank("주방장");
        else if((customer.getMyPosts()>=10)&&(customer.getMyComments()>=10)&&(customer.getMyLikes()>=10))
            customer.setRank("요리사");
        else
            customer.setRank("초보");
        return new LoginRes(accessToken);
    }
    public LoginRes googleLogin(GoogleLoginReq googleLoginReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByEmail(googleLoginReq.getEmail());
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        CustomerEntity customer = opCustomer.get();
        String accessToken= jwtUtil.createToken(customer.getCustomerId());
        if((customer.getMyPosts()>=50)&&(customer.getMyComments()>=50)&&(customer.getMyLikes()>=50))
            customer.setRank("주방장");
        else if((customer.getMyPosts()>=10)&&(customer.getMyComments()>=10)&&(customer.getMyLikes()>=10))
            customer.setRank("요리사");
        else
            customer.setRank("초보");
        return new LoginRes(accessToken);

    }
    public boolean changeCustomerName(Long customerId, ChangeCustomerNameReq changeCustomerNameReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        Optional<CustomerEntity> opName=customerRepository.findCustomerEntityByCustomerName(changeCustomerNameReq.getCustomerName());
        if(opName.isPresent())
            return false;
        CustomerEntity customer=opCustomer.get();
        customer.setCustomerName(changeCustomerNameReq.getCustomerName());
        customerRepository.save(customer);
        return true;
    }
    public GetMyPageRes getMyPage(Long customerId){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        if((customer.getMyPosts()>=50)&&(customer.getMyComments()>=50)&&(customer.getMyLikes()>=50))
            customer.setRank("주방장");
        else if((customer.getMyPosts()>=10)&&(customer.getMyComments()>=10)&&(customer.getMyLikes()>=10))
            customer.setRank("요리사");
        else
            customer.setRank("초보");
        return new GetMyPageRes(customer.getCustomerName(),customer.getRank(),customer.getMyLikes(),customer.getMyComments(),customer.getMyPosts());
    }
    public boolean validatePassword(Long customerId, ValidatePasswordReq validatePasswordReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(validatePasswordReq.getPassword(), customer.getPassword()))
            return false;
        return true;
    }
    public boolean changePassword(Long customerId, ChangePasswordReq changePasswordReq){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_NAME_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String encodedPassword= encoder.encode(changePasswordReq.getPassword());
        if(encodedPassword.isEmpty())
            throw new GeneralException(ErrorCode.BLANK_PASSWORD);
        customer.setPassword(encodedPassword);
        customerRepository.save(customer);
        return true;
    }
    public ManageRes getManage(Long customerId){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        if((customer.getMyPosts()>=50)&&(customer.getMyComments()>=50)&&(customer.getMyLikes()>=50))
            customer.setRank("주방장");
        else if((customer.getMyPosts()>=10)&&(customer.getMyComments()>=10)&&(customer.getMyLikes()>=10))
            customer.setRank("요리사");
        else
            customer.setRank("초보");
        return new ManageRes(customer.getCustomerName(), customer.getEmail(), customer.getRank());
    }
    public List<MyPostRes> getMyPosts(Long customerId){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        List<PostEntity> posts=customer.getPosts();
        if(posts.isEmpty())
            throw new GeneralException(ErrorCode.POST_DOES_NOT_EXIST);
        return posts.stream()
                .map(post->
                new MyPostRes(post.getPostId(), post.getTitle(), post.getUpdateDate())
        ).collect(Collectors.toList());
    }
    public List<MyCommentRes> getMyComments(Long customerId){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        List<CommentEntity> comments=commentRepository.findCommentEntitiesByCustomer(customer);
        if(comments.isEmpty())
            throw new GeneralException(ErrorCode.COMMENT_DOES_NOT_EXIST);
        return comments.stream()
                .map(comment->
                        new MyCommentRes(comment.getPost().getPostId(),comment.getCommentId(), comment.getPost().getTitle(),comment.getContents(),comment.getTime())
                ).collect(Collectors.toList());
    }
    public boolean withdraw(Long customerId){
        Optional<CustomerEntity> opCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(opCustomer.isEmpty())
            throw new GeneralException(ErrorCode.CUSTOMER_DOES_NOT_EXIST);
        CustomerEntity customer=opCustomer.get();
        customerRepository.delete(customer);
        Optional<CustomerEntity> delCustomer=customerRepository.findCustomerEntityByCustomerId(customerId);
        if(delCustomer.isPresent())
            return false;
        else
            return true;
    }
}
