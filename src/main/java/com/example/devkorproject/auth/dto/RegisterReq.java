package com.example.devkorproject.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class RegisterReq {
    private String customerName;

    private String email;

    private String password;

    private String rank;

    private Long myPosts;

    private Long myLikes;

    private Long myComments;
}
