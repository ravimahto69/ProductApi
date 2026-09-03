package com.zest.productapi.service;

import com.zest.productapi.dto.ItemRequest;
import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.entity.Item;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ItemRepository;
import com.zest.productapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;

    public ItemResponse addItem(Long productId, ItemRequest request) {
        Product product = findProduct(productId);

        Item item = new Item();
        item.setQuantity(request.getQuantity());
        item.setProduct(product);

        return mapToResponse(itemRepository.save(item));
    }

    public ItemResponse createItem(Long productId, ItemRequest request) {
        return addItem(productId, request);
    }

    public ItemResponse getItemById(Long id) {
        return mapToResponse(findItem(id));
    }

    public Page<ItemResponse> getItemsByProduct(Long productId, Pageable pageable) {
        findProduct(productId);
        return itemRepository.findByProduct_Id(productId, pageable)
                .map(this::mapToResponse);
    }

    public ItemResponse updateItem(Long id, ItemRequest request) {
        Item item = findItem(id);
        Product product = findProduct(request.getProductId());

        item.setQuantity(request.getQuantity());
        item.setProduct(product);

        return mapToResponse(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        itemRepository.delete(findItem(id));
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    private ItemResponse mapToResponse(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setQuantity(item.getQuantity());
        return response;
    }

}
