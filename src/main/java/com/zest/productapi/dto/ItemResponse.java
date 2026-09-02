package com.zest.productapi.dto;

import lombok.Data;

@Data
public class ItemResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
}
