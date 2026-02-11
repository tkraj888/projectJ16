package com.spring.jwt.entity;

import com.spring.jwt.Enums.ProductSectionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Table(name = "product_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductSectionType sectionType;

    /* ✅ LIST OF STRINGS ALLOWED HERE */
    @ElementCollection
    @CollectionTable(
            name = "product_section_contents",
            joinColumns = @JoinColumn(name = "section_id")
    )
    @Column(columnDefinition = "TEXT")
    private List<String> content;

    /* 🔗 BACK REFERENCE */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
