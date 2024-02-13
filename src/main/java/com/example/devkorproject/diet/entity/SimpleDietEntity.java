package com.example.devkorproject.diet.entity;

import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleDietEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simpleDietId;

    @Column(name = "dietName" ,nullable = false, length = 20)
    private String dietName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "time", nullable = false)
    private Long time;

    @Column(name = "type")
    private String type;

    @Column(name = "heart", nullable = false)
    @ColumnDefault("false")
    private boolean heart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "babyId")
    private BabyEntity baby;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dietId")
    private DietEntity diet;
}
