package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface LikeRepository extends JpaRepository<LikeEntity,Long> {
}
