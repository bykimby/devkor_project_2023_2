package com.example.devkorproject.fridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FridgeUpReq {

    private Long fridgeId;
    private String ingredients;
    private boolean active;
    private String emoticon;
}
