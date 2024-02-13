package com.example.devkorproject.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@Setter
public class AlarmResDto {
    private String message;
    private LocalDateTime date;
    private Long postId;
}
