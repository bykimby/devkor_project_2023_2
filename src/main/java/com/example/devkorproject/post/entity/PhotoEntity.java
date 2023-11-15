package com.example.devkorproject.post.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;
    @Lob
    @Column(name="data",nullable = false)
    private byte[] data;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId")
    private PostEntity post;
}
