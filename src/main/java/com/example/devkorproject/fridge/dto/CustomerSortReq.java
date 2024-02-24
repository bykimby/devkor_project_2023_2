package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CustomerSortReq {

    private List<Long> fridgeIdOrder;
}
