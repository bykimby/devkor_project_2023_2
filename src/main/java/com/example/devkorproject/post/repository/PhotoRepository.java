package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PhotoRepository extends JpaRepository<PhotoEntity, Long>{
}
