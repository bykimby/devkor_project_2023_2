package com.example.devkorproject.post.entity;

import com.example.devkorproject.alarm.controller.AlarmController;
import com.example.devkorproject.alarm.entity.AlarmEntity;
import com.example.devkorproject.customer.entity.CustomerEntity;
import com.example.devkorproject.diet.entity.SimpleDietEntity;
import com.example.devkorproject.post.dto.CommentRes;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Builder.Default
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<LikeEntity> likeEntities=new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy="post",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<CommentEntity> commentEntities=new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ScrapEntity> scrapEntities=new ArrayList<>();

//    @OneToMany(mappedBy="post",cascade = CascadeType.REMOVE,orphanRemoval = true)
//    private Set<PhotoEntity> photos;
//    //중복 x 허용하는 것으로 list보다 관리 용이
//    //orphan removal은 부모 엔티티 삭제되면 자식 entity들도 삭제되도록 함
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<PhotoEntity> photo = new ArrayList<PhotoEntity>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private CustomerEntity customer;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    @Builder.Default
    private List<AlarmEntity> alarmEntities = new ArrayList<AlarmEntity>();


}
