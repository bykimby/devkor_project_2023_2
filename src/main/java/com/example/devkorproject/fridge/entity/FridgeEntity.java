package com.example.devkorproject.fridge.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime date;
    private boolean active;
    private String emoticon;
    private Long customerOrder;
    @ManyToOne
    @JoinColumn(name="customerId")
    private CustomerEntity customer;
}
