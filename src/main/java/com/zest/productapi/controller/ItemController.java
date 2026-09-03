package com.zest.productapi.controller;

import com.zest.productapi.dto.ItemRequest;
import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/products/{productId}/items")
    public ResponseEntity<ItemResponse> addItem(
            @PathVariable Long productId,
            @Valid @RequestBody ItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.addItem(productId, request));
    }

    @GetMapping("/products/{productId}/items")
    public Page<ItemResponse> getItemsByProduct(
            @PathVariable Long productId,
            Pageable pageable) {
        return itemService.getItemsByProduct(productId, pageable);
    }

    @GetMapping("/items/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PutMapping("/items/{id}")
    public ItemResponse updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request) {
        return itemService.updateItem(id, request);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
