package com.spring.jwt.ProductPhoto;


import com.spring.jwt.Enums.ImageType;
import com.spring.jwt.Enums.PhotoType;

import org.springframework.web.multipart.MultipartFile;

public interface ProductPhotoService {


    ProductPhotoResponseUploadDTO uploadProductPhoto(Long productId, ImageType photoType, MultipartFile file);

    ProductPhotoResponseDTO getPhotoById(Long imageId);

    ProductPhotoResponseDTO getPhotoByProductId(Long productId);

    ProductPhotoResponseDTO updateProductImage(Long imageId, MultipartFile file);
}
