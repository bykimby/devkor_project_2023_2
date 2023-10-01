package com.example.devkorproject.post.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "updateDate", nullable = false)
    private LocalDate updateDate;

    @Column(name = "comments", nullable = false)
    private Long comments;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "body", nullable = false, length = 500)
    private String body;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "photo")
    private String photoUrl;

    @Column(name = "scrap", nullable = false)
    private Long scrap;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;
}
