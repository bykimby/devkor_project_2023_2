package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
}
