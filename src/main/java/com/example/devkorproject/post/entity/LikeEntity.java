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
public class LikeEntity {
    @Id
    @GeneratedValue
    Long Likeid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId")
    PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="customerId")
    CustomerEntity customer;
}
