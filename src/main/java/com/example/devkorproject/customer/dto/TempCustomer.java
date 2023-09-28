package com.example.devkorproject.customer.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class TempCustomer {
    private String customerName;
    private String email;
    private String password;
    private String imageUrl;
    private String rank;
    private Long myPosts;
    private Long myLikes;
    private Long myComments;
}
