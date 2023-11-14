package com.example.devkorproject.diet.dto;
import lombok.*;
@Data
@NoArgsConstructor
public class ChatGptMessage {
    private String role;
    private String content;

    @Builder
    public ChatGptMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}