package com.spring.jwt.ProductPhoto;


import com.spring.jwt.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPhotoRepository extends JpaRepository<ProductImage, Long> {

    boolean existsByProduct_ProductId(Long productId);

    Optional<ProductImage> findByProduct_ProductId(Long productId);
}
