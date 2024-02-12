package com.example.devkorproject.post.repository;

import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.post.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    List<CommentEntity> findCommentEntitiesByCustomer(CustomerEntity customer);
}
