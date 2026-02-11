package com.spring.jwt.Product;

import com.spring.jwt.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByProductType(Product.ProductType productType, Pageable pageable);

    Page<Product> findByProductTypeAndCategory(Product.ProductType productType, Product.Category category, Pageable pageable);
}
