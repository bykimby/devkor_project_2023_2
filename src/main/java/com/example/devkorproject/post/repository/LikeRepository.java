package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface LikeRepository extends JpaRepository<LikeEntity,Long> {
    Optional<LikeEntity> findByCustomer_CustomerIdAndPost_PostId(Long customerId, Long postId);
}
