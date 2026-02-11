package com.spring.jwt.Product;

import com.spring.jwt.ProductPhoto.ProductPhotoRepository;
import com.spring.jwt.entity.Product;
import com.spring.jwt.entity.ProductSection;
import com.spring.jwt.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductPhotoRepository productPhotoRepository;


    @Override
    public ProductDTO create(ProductDTO dto) throws BadRequestException {

        if (dto == null) {
            throw new BadRequestException("Product payload cannot be null");
        }

        if (dto.getProductName() == null || dto.getProductName().isBlank()) {
            throw new BadRequestException("Product name is required");
        }

        if (dto.getProductType() == null) {
            throw new BadRequestException("Product type is required");
        }

        Product product = mapToEntity(dto);
        Product saved = productRepository.save(product);

        return mapToDto(saved);
    }


    @Override
    @Transactional
    public ProductDTO getById(Long productId) throws BadRequestException {
        Product product=productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        ProductDTO dto = mapToDto(product);
        ProductPhotoDTO photoDTO = new ProductPhotoDTO();
        productPhotoRepository.findByProduct_ProductId(productId)
                .ifPresentOrElse(
                        photo->{
                            photoDTO.setImageUrl(photo.getImageUrl());
                            photoDTO.setUploadedAt(photo.getUploadedAt());
                            photoDTO.setMessage("Product Photo Found");
                        },
                        ()->{
                            photoDTO.setMessage("Product Photo Not Uploaded");
                        }
                );
        dto.setPhotoDTO(photoDTO);
        return dto;
    }

    @Override
    @Transactional
    public List<ProductDTO> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public Page<ProductDTO> getAllByProductType(Product.ProductType productType , Pageable pageable){
        Page<Product> page = productRepository.findByProductType(productType, pageable);

        if (pageable.getPageNumber()>=page.getTotalPages()
        && page.getTotalPages()>0){
            throw new ResourceNotFoundException("Page not found. Requested Page: " + pageable.getPageNumber());
        }
        return page.map(this::mapToDto);
    }

    @Override
    @Transactional
    public Page<ProductDTO> getAllByProductTypeAndCategory(Product.ProductType productType, Product.Category category, Pageable pageable){
        Page<Product> page;
        if (category==Product.Category.ALL){
            page=productRepository.findByProductType(productType, pageable);

        }else {
            page=productRepository.findByProductTypeAndCategory(productType, category, pageable);
        }
        if (pageable.getPageNumber()>=page.getTotalPages()
         && page.getTotalPages()>0){
            throw new ResourceNotFoundException("Page not found. Requested Page: " + pageable.getPageNumber());
        }
        return page.map(this::mapToDto);
    }



    @Override
    public ProductDTO patch(Long id, ProductDTO dto) throws BadRequestException {

        if (dto == null) {
            throw new BadRequestException("Patch payload cannot be null");
        }

        Product product = getProduct(id);

        if (dto.getProductName() != null) {
            product.setProductName(dto.getProductName());
        }

        if (dto.getProductType() != null) {
            product.setProductType(dto.getProductType());
        }

        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }

        if (dto.getOffers() != null) {
            product.setOffers(dto.getOffers());
        }

        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }

        if (dto.getSections() != null) {
            product.getSections().clear();
            product.getSections().addAll(mapSections(dto.getSections(), product));
        }

        return mapToDto(product);
    }


    @Override
    public void delete(Long id) throws BadRequestException {
        Product product = getProduct(id);
        productRepository.delete(product);
    }


    private Product getProduct(Long id) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException("Product id cannot be null");
        }

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Product mapToEntity(ProductDTO dto) throws BadRequestException {

        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setProductType(dto.getProductType());
        product.setPrice(dto.getPrice());
        product.setOffers(dto.getOffers());
        product.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getSections() != null) {
            product.setSections(mapSections(dto.getSections(), product));
        }

        return product;
    }

    private List<ProductSection> mapSections(List<ProductSectionDTO> dtos, Product product) throws BadRequestException {

        if (dtos.isEmpty()) {
            throw new BadRequestException("Product sections cannot be empty");
        }

        return dtos.stream().map(dto -> {

            if (dto.getSectionType() == null) {
                try {
                    throw new BadRequestException("Section type is required");
                } catch (BadRequestException e) {
                    throw new RuntimeException(e);
                }
            }

            ProductSection section = new ProductSection();
            section.setSectionType(dto.getSectionType());
            section.setContent(dto.getContent());
            section.setProduct(product);
            return section;

        }).toList();
    }

    private ProductDTO mapToDto(Product product) {

        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductType(product.getProductType());
        dto.setPrice(product.getPrice());
        dto.setOffers(product.getOffers());
        dto.setActive(product.getActive());

        if (product.getSections() != null) {
            dto.setSections(product.getSections().stream().map(s -> {
                ProductSectionDTO sd = new ProductSectionDTO();
                sd.setSectionType(s.getSectionType());
                sd.setContent(s.getContent());
                return sd;
            }).toList());
        }

        return dto;
    }
}
