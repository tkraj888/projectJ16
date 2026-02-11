package com.spring.jwt.entity;

import com.spring.jwt.Enums.ImageType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_images",
        indexes = {
                @Index(name = "idx_product_images_product_id", columnList = "product_id")
        }
)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;

    private ImageType imageType;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}