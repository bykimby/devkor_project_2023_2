package com.example.devkorproject.baby.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class BabyModifyResDto {
    private Long babyId;
    private String babyName;
    private String birth;
    private String allergy;
    private String needs;
}
