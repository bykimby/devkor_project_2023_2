package com.example.devkorproject.post.repository;

import com.example.devkorproject.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findTop20ByTitleContainingOrBodyContainingOrderByUpdateDateDesc(String titleKeyword, String bodyKeyword);
    List<PostEntity> findNext20ByTitleContainingOrBodyContainingAndUpdateDateBeforeOrderByUpdateDateDesc(String titleKeyword, String bodyKeyword, LocalDateTime updateDate);
    List<PostEntity> findNext20ByCustomer_CustomerIdAndUpdateDateBeforeOrderByUpdateDateDesc(Long customerId, LocalDateTime updateDate);
    List<PostEntity> findTop20ByCustomer_CustomerIdOrderByUpdateDateDesc(Long customerId);
    List<PostEntity> findTop20ByTypeOrderByUpdateDateDesc(String type);
    List<PostEntity> findTop20ByOrderByUpdateDateDesc();
    List<PostEntity> findTop20ByOrderByLikesDesc();
    List<PostEntity> findTop20ByUpdateDateBeforeOrderByUpdateDateDesc(LocalDateTime updateDate);
    List<PostEntity> findTop20ByTypeAndUpdateDateBeforeOrderByUpdateDateDesc(String type, LocalDateTime updateDate);

    @Query("SELECT p FROM PostEntity p WHERE p.updateDate >= :weekAgo ORDER BY p.likes DESC")
    List<PostEntity> findTop10ByLikesOrderByUpdateDateDesc(LocalDateTime weekAgo);

    default List<PostEntity> findTop10ByLikesWithinLastWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        return findTop10ByLikesOrderByUpdateDateDesc(weekAgo);
    }
    @Query("SELECT p FROM PostEntity p WHERE p.type = :type ORDER BY p.likes DESC")
    List<PostEntity> findTop20ByTypeLikesOrderByLikesDesc(@Param("type") String type);
    List<PostEntity> findByLikesLessThanAndPostIdGreaterThanOrderByLikesDescPostIdAsc(
            Long likes, Long startPostId, Pageable pageable);
    List<PostEntity> findByTypeAndLikesLessThanEqualAndPostIdLessThanOrderByLikesDescUpdateDateAsc(
            String type,Long likes, Long startPostId, Pageable pageable);

    Optional<String> findCustomerFcmTokenByPostId(Long postId);
}
