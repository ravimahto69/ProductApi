package com.zest.productapi.service;

import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.dto.ProductResponse;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldCreateProduct() {
        ProductRequest request = request("Laptop");
        Product saved = product(1L, "Laptop");
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse result = productService.createProduct(request, "admin");

        assertEquals("Laptop", result.getProductName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProduct_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, "Laptop")));

        ProductResponse result = productService.getProductById(1L);

        assertEquals("Laptop", result.getProductName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProduct_shouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void updateProduct_shouldUpdateProduct() {
        Product product = product(1L, "Old name");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        ProductResponse result = productService.updateProduct(1L, request("New name"), "admin");

        assertEquals("New name", result.getProductName());
        assertEquals("admin", product.getModifiedBy());
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        Product product = product(1L, "Laptop");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void getProducts_shouldReturnPaginatedProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(productRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(List.of(product(1L, "Laptop"))));

        assertEquals(1, productService.getProducts(pageRequest).getTotalElements());
    }

    private ProductRequest request(String name) {
        ProductRequest request = new ProductRequest();
        request.setProductName(name);
        return request;
    }

    private Product product(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setProductName(name);
        product.setCreatedBy("admin");
        product.setItems(List.of());
        return product;
    }
}
