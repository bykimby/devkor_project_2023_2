package com.example.devkorproject.baby.dto;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class BabyModifyReqDto {
    private Long customerId;
    private Long babyId;
    private String babyName;
    private String birth;
    private String allergy;
    private String needs;
}
