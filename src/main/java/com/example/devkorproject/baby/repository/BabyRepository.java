package com.example.devkorproject.baby.repository;
import com.example.devkorproject.baby.entity.BabyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface BabyRepository extends JpaRepository<BabyEntity,Long> {
    Optional<BabyEntity> findBabyEntityByBabyId(Long babyId);
    List<BabyEntity> findBabyByCustomerCustomerId(Long customerId);
}
