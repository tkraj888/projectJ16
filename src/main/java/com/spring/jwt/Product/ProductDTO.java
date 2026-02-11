package com.spring.jwt.Product;

import com.spring.jwt.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long productId;
    private String productName;
    private Product.ProductType productType;
    private Product.Category category;
    private Double price;
    private Double offers;
    private Boolean active;
    private List<ProductSectionDTO> sections;
    private ProductPhotoDTO photoDTO;
}
