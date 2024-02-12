package com.example.devkorproject.customer.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class GetMyPageRes {
    private String customerName;
    private String rank;
    private Long myLikes;
    private Long myComments;
    private Long myPosts;
}
