package com.example.devkorproject.diet.entity;

import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "ingredients", nullable = false)
    private String ingredients;

    @Column(name = "recipe", nullable = false, length = 512)
    private String recipe;

    @Column(name = "available")
    private String available;

    @Column(name = "allergy")
    private String allergy;

    @Column(name = "needs")
    private String needs;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "image", nullable = false)
    private String imageUrl;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "diet", orphanRemoval = true)
    @Builder.Default
    private List<SimpleDietEntity> simplediets = new ArrayList<SimpleDietEntity>();
}
