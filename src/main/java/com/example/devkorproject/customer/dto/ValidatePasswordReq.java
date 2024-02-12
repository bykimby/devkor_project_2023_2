package com.example.devkorproject.customer.dto;

import lombok.*;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class ValidatePasswordReq {
    private String password;
}
