package com.zest.productapi.service;


import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.dto.ProductResponse;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request, String username) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setCreatedBy(username);
        product.setCreatedOn(LocalDateTime.now());

        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse getProductById(Long id) {
        return mapToResponse(findProduct(id));
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToResponse);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, String username) {
        Product product = findProduct(id);
        product.setProductName(request.getProductName());
        product.setModifiedBy(username);
        product.setModifiedOn(LocalDateTime.now());

        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(Math.toIntExact(product.getId()));
        response.setProductName(product.getProductName());
        response.setCreatedBy(product.getCreatedBy());
        response.setCreatedOn(product.getCreatedOn());
        response.setModifiedBy(product.getModifiedBy());
        response.setModifiedOn(product.getModifiedOn());
        response.setItems(product.getItems().stream()
                .map(item -> {
                    ItemResponse itemResponse = new ItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setProductId(product.getId());
                    itemResponse.setQuantity(item.getQuantity());
                    return itemResponse;
                })
                .collect(Collectors.toList()));
        return response;
    }
}