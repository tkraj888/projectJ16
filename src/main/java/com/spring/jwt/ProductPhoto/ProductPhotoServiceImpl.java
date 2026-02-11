package com.spring.jwt.ProductPhoto;

import com.spring.jwt.Enums.ImageType;
import com.spring.jwt.Enums.PhotoType;
import com.spring.jwt.Product.ProductRepository;
import com.spring.jwt.entity.Product;
import com.spring.jwt.entity.ProductImage;
import com.spring.jwt.exception.DocumentAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
/**
 * Service implementation responsible for managing Product Photo operations.
 *
 * Responsibilities:
 * - Upload product photo
 * - Fetch photo by image ID or product ID
 * - Update (patch) product image only
 *
 * Design Notes:
 * - Base64 image is stored directly in DB
 * - One photo per product is enforced
 * - All write operations are transactional
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPhotoServiceImpl implements ProductPhotoService {

    /**
     * Repository for Product Photo persistence operations
     */

    private final ProductPhotoRepository productPhotoRepository;
    /**
     * Repository for Product persistence operations
     */

    private final ProductRepository productRepository;

    /**
     * Upload a product photo.
     *
     * Business Rules:
     * - Product must exist
     * - Only one photo allowed per product
     * - Only valid image files are accepted
     *
     * @param productId Product ID
     * @param photoType Type of photo
     * @param file Image file
     * @return ProductPhotoResponseUploadDTO
     */
    @Override
    @Transactional
    public ProductPhotoResponseUploadDTO uploadProductPhoto(
            Long productId,
            ImageType photoType,
            MultipartFile file
    ) {
        validateProductId(productId);
        validateImage(file);
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: " + productId
                        )
                );
        if (productPhotoRepository.existsByProduct_ProductId(productId)) {
            throw new DocumentAlreadyExistsException(
                    "Product photo already exists for product ID: " + productId
            );
        }
        try {
            long startTime = System.currentTimeMillis();
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageType(photoType);
            image.setImageUrl(encodeBase64(file));
            image.setUploadedAt(LocalDateTime.now());
            ProductImage saved = productPhotoRepository.save(image);
            long totalTime = System.currentTimeMillis() - startTime;
            log.info(
                    "Product photo uploaded successfully for productId={} in {} ms", productId, totalTime);
            return mapToResponseUpload(saved);
        } catch (Exception e) {
            log.error(
                    "Failed to upload product photo for product {}", productId, e);
            throw new RuntimeException("Failed to upload product photo", e);
        }
    }
    /**
     * Fetch product photo using image ID.
     *
     * @param imageId Image ID
     * @return ProductPhotoResponseDTO
     */
    @Override
    public ProductPhotoResponseDTO getPhotoById(Long imageId) {
        ProductImage image = productPhotoRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Product photo not found with ID: " + imageId
                        )
                );
        return mapToResponse(image);
    }
    /**
     * Fetch product photo using product ID.
     *
     * @param productId Product ID
     * @return ProductPhotoResponseDTO

     */

    @Override

    public ProductPhotoResponseDTO getPhotoByProductId(Long productId) {
        ProductImage image = productPhotoRepository
                .findByProduct_ProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Product photo not found for product ID: " + productId
                        )
                );
        return mapToResponse(image);
    }
    /**
     * Update (replace) product image only.
     *
     * @param imageId Image ID
     * @param file New image
     * @return Updated ProductPhotoResponseDTO
     */
    @Override
    @Transactional
    public ProductPhotoResponseDTO updateProductImage(
            Long imageId,
            MultipartFile file
    ) {
        validateImage(file);
        ProductImage image = productPhotoRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product photo not found with ID: " + imageId
                        )
                );
        try {
            image.setImageUrl(encodeBase64(file));
            image.setUploadedAt(LocalDateTime.now());
            return mapToResponse(productPhotoRepository.save(image));
        } catch (Exception e) {
            log.error("Failed to update product photo {}", imageId, e);
            throw new RuntimeException("Failed to update product photo", e);
        }
    }



    private void validateProductId(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID must not be null");
        }
    }


    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        if (file.getContentType() == null ||
                !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    "Image size must be less than 5MB"
            );
        }
    }
    private String encodeBase64(MultipartFile file) {
        try {
            return Base64.getEncoder()
                    .encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to process image file",
                    e
            );
        }
    }

    /* =======================

       DTO Mappers

       ======================= */

    private ProductPhotoResponseDTO mapToResponse(ProductImage entity) {

        ProductPhotoResponseDTO dto = new ProductPhotoResponseDTO();
        dto.setImageId(entity.getImageId());
        dto.setProductId(entity.getProduct().getProductId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setUploadedAt(entity.getUploadedAt());
        return dto;
    }

    private ProductPhotoResponseUploadDTO mapToResponseUpload(ProductImage entity) {
        ProductPhotoResponseUploadDTO dto =
                new ProductPhotoResponseUploadDTO();
        dto.setImageId(entity.getImageId());
        dto.setProductId(entity.getProduct().getProductId());
        dto.setUploadedAt(entity.getUploadedAt());

        return dto;

    }

}
