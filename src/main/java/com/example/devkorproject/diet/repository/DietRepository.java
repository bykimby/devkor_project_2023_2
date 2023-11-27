package com.example.devkorproject.diet.repository;

import com.example.devkorproject.baby.entity.BabyEntity;
import com.example.devkorproject.diet.entity.DietEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface DietRepository extends JpaRepository<DietEntity,Long> {
}
