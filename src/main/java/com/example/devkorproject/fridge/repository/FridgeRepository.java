package com.example.devkorproject.fridge.repository;

import com.example.devkorproject.fridge.entity.FridgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface FridgeRepository extends JpaRepository<FridgeEntity,Long> {
    List<FridgeEntity> findByCustomerCustomerId(Long customerId);
}
