package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByTitleContainingOrBodyContaining(String titleKeyword,String bodyKeyword);
    List<PostEntity> findByCustomer_CustomerId(Long customerId);
    List<PostEntity> findByCategory(String category);
}
