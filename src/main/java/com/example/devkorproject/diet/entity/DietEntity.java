package com.example.devkorproject.diet.entity;

import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dietId;

    @Column(name = "dietName" ,nullable = false, length = 20)
    private String dietName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "ingredients", nullable = false)
    private String ingredients;

    @Column(name = "recipe", nullable = false)
    private String recipe;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "time", nullable = false)
    private Long time;

    @Column(name = "info", nullable = false)
    private String info;

    @Column(name = "available")
    private String available;

    @Column(name = "allergy")
    private String allergy;

    @Column(name = "needs")
    private String needs;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "image", nullable = false)
    private MultipartFile image;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "babyId")
    private BabyEntity baby;
}
