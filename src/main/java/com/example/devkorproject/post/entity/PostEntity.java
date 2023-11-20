package com.example.devkorproject.post.entity;

import com.example.devkorproject.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @UpdateTimestamp
    @Column(name = "updateDate", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "comments", nullable = false)
    private Long comments;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "body", nullable = false, length = 500)
    private String body;

    @Column(name = "scrap", nullable = false)
    private Long scrap;

    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(mappedBy="post",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Set<PhotoEntity> photos;
    //중복 x 허용하는 것으로 list보다 관리 용이
    //orphan removal은 부모 엔티티 삭제되면 자식 entity들도 삭제되도록 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;
}
