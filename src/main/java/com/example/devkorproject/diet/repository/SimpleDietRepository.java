package com.example.devkorproject.diet.repository;

import com.example.devkorproject.diet.entity.DietEntity;
import com.example.devkorproject.diet.entity.SimpleDietEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SimpleDietRepository extends JpaRepository<SimpleDietEntity, Long> {
    Optional<SimpleDietEntity> findBySimpleDietId(Long simpleDietId);
    List<SimpleDietEntity> findByCustomerCustomerIdAndHeart(Long customerId, boolean heart);
}
