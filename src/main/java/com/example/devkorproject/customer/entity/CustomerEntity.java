package com.example.devkorproject.customer.entity;

import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.diet.entity.SimpleDietEntity;
import com.example.devkorproject.post.entity.PhotoEntity;
import com.example.devkorproject.post.entity.PostEntity;
import jakarta.persistence.*;
import lombok.*;

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
    //@OneToOne
    //@Column(name = "image")
    //private String image;

    @Column(name = "rank", nullable = false)
    private String rank;

    @Column(name = "myPosts", nullable = false)
    private Long myPosts;

    @Column(name = "myLikes", nullable = false)
    private Long myLikes;

    @Column(name = "myComments", nullable = false)
    private Long myComments;

    @Column(name="authority")
    private String authority;

    @OneToMany(mappedBy = "customer",orphanRemoval = true)
    @Builder.Default
    private List<BabyEntity> babies = new ArrayList<BabyEntity>();

    @OneToMany(mappedBy = "customer",orphanRemoval = true)
    @Builder.Default
    private List<PostEntity> posts = new ArrayList<PostEntity>();
    
    @OneToMany(mappedBy = "customer",orphanRemoval = true)
    @Builder.Default
    private List<SimpleDietEntity> simpleDiets = new ArrayList<SimpleDietEntity>();
}
