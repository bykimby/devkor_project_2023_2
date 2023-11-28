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

    @Column(nullable = false)
    private String origFileName; // 파일 원본명

    @Column(nullable = false)
    private String filePath; //파일 저장 경로

    @Column(nullable = false)
    private Long fileSize;

    @Lob
    @Column(name="data")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId")
    private PostEntity post;

}
