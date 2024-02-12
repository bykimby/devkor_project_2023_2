package com.example.devkorproject.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ManageRes {
    private String customerName;
    private String email;
    private String rank;
}
