package com.example.devkorproject.diet.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "recipe", nullable = false, length = 1024)
    private String recipe;

    @Column(name = "available")
    private String available;

    @Column(name = "allergy")
    private String allergy;

    @Column(name = "needs")
    private String needs;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "diet", orphanRemoval = true)
    @Builder.Default
    private List<SimpleDietEntity> simpleDiets = new ArrayList<SimpleDietEntity>();
}
