package com.example.devkorproject.fridge.repository;

import com.example.devkorproject.fridge.entity.FridgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface FridgeRepository extends JpaRepository<FridgeEntity,Long> {
    List<FridgeEntity> findByCustomerCustomerId(Long customerId);
    @Query("SELECT f.ingredients FROM FridgeEntity f WHERE f.customer.customerId = :customerId AND f.active = true")
    List<String> findActiveIngredientsByCustomerId(@Param("customerId") Long customerId);

}
