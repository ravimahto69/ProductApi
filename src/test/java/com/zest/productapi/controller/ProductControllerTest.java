package com.zest.productapi.controller;

import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.dto.ProductResponse;
import com.zest.productapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Principal principal;

    @InjectMocks
    private ProductController productController;

    @Test
    void createProduct_shouldReturnCreatedResponse() {
        ProductResponse response = new ProductResponse();
        when(principal.getName()).thenReturn("admin");
        when(productService.createProduct(any(ProductRequest.class), any(String.class))).thenReturn(response);

        assertEquals(201, productController.createProduct(new ProductRequest(), principal).getStatusCode().value());
        verify(productService).createProduct(any(ProductRequest.class), org.mockito.ArgumentMatchers.eq("admin"));
    }

    @Test
    void updateProduct_shouldDelegateToService() {
        ProductRequest request = new ProductRequest();
        ProductResponse response = new ProductResponse();
        when(principal.getName()).thenReturn("admin");
        when(productService.updateProduct(1L, request, "admin")).thenReturn(response);

        assertEquals(response, productController.updateProduct(1L, request, principal));
        verify(productService).updateProduct(1L, request, "admin");
    }

    @Test
    void deleteProduct_shouldReturnNoContent() {
        assertEquals(204, productController.deleteProduct(1L).getStatusCode().value());
        verify(productService).deleteProduct(1L);
    }
}
