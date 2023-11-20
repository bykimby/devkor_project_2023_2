package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findTop20ByTitleContainingOrBodyContainingOrderByUpdateDateDesc(String titleKeyword, String bodyKeyword);
    List<PostEntity> findNext20ByTitleContainingOrBodyContainingAndUpdateDateBeforeOrderByUpdateDateDesc(String titleKeyword, String bodyKeyword, LocalDateTime updateDate);
    List<PostEntity> findNext20ByCustomer_CustomerIdAndUpdateDateBeforeOrderByUpdateDateDesc(Long customerId, LocalDateTime updateDate);
    List<PostEntity> findTop20ByCustomer_CustomerIdOrderByUpdateDateDesc(Long customerId);
    List<PostEntity> findTop20ByTypeOrderByUpdateDateDesc(String type);
    List<PostEntity> findTop20ByOrderByUpdateDateDesc();
    List<PostEntity> findTop20ByUpdateDateBeforeOrderByUpdateDateDesc(LocalDateTime updateDate);
    List<PostEntity> findTop20ByTypeAndUpdateDateBeforeOrderByUpdateDateDesc(String type, LocalDateTime updateDate);


    Page<PostEntity> findAll(Pageable pageable);

}
