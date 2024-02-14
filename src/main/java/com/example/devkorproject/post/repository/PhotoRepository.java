package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PhotoRepository extends JpaRepository<PhotoEntity, Long>{
    List<PhotoEntity> findByFilePathNotIn(List<String> filePath);
}
