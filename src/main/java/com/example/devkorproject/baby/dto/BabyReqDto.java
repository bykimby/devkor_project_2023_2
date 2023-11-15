package com.example.devkorproject.baby.dto;
import lombok.*;
@Data
@AllArgsConstructor
@Getter
@Setter
public class BabyReqDto {
    private Long customerId;
    private String babyName;
    private String birth;
    private String allergy;
    private String needs;
}
