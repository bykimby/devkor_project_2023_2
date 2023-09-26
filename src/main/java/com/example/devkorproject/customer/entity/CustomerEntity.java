package com.example.devkorproject.customer.entity;

import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.diet.entity.DietEntity;
import com.example.devkorproject.post.entity.PostEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "customerName" ,nullable = false, length = 20)
    private String customerName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "image")
    private MultipartFile image;

    @Column(name = "rank", nullable = false)
    private String rank;

    @Column(name = "myPosts", nullable = false)
    private Long myPosts;

    @Column(name = "myLikes", nullable = false)
    private Long myLikes;

    @Column(name = "myComments", nullable = false)
    private Long myComments;

    @OneToMany(mappedBy = "customer")
    private List<BabyEntity> babies = new ArrayList<BabyEntity>();

    @OneToMany(mappedBy = "customer")
    private List<PostEntity> posts = new ArrayList<PostEntity>();
    
    @OneToMany(mappedBy = "customer")
    private List<DietEntity> diets = new ArrayList<DietEntity>();
    //연관관계메소드?
    //생성메소드??

}
