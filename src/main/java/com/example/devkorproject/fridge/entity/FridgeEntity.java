package com.example.devkorproject.fridge.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne
    @JoinColumn(name="customerId")
    private CustomerEntity customer;
}
