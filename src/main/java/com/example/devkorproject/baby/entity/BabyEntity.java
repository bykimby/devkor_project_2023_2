package com.example.devkorproject.baby.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.diet.entity.DietEntity;
import com.example.devkorproject.diet.entity.SimpleDietEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BabyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long babyId;

    @Column(name = "babyName" ,nullable = false, length = 20)
    private String babyName;
    
    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Column(name = "allergy")
    private String allergy;

    @Column(name = "needs")
    private String needs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;

    @OneToMany(mappedBy = "baby",orphanRemoval = true)
    @Builder.Default
    private List<SimpleDietEntity> simpleDiets = new ArrayList<SimpleDietEntity>();

}
