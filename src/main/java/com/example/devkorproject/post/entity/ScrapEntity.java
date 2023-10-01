package com.example.devkorproject.post.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long scrapId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId")
    PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customerId")
    CustomerEntity customer;
}
