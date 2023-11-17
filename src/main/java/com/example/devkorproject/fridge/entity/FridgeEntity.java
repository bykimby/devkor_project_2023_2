package com.example.devkorproject.fridge.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FridgeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long frigeId;
    private String ingredients;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name="customerId")
    private CustomerEntity customer;
}
