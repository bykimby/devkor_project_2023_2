package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomerSortReq {
    private Long customerId;
    private List<Long> fridgeIdOrder;
}
