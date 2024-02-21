package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.ScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ScrapRepository extends JpaRepository<ScrapEntity,Long> {
    List<ScrapEntity> findByCustomer_CustomerId(Long customerId);
    Optional<ScrapEntity> findByCustomer_CustomerIdAndPost_PostId(Long customerId, Long postId);

}
