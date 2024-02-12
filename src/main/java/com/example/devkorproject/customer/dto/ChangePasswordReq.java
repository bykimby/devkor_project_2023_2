package com.example.devkorproject.customer.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
@Getter
@Setter
public class ChangePasswordReq {
    private String password;
}
