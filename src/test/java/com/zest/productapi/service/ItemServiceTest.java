package com.zest.productapi.service;

import com.zest.productapi.dto.ItemRequest;
import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.entity.Item;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ItemRepository;
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
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void addItem_shouldCreateItem() {
        Product product = product(2L);
        Item saved = item(1L, product, 15);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemResponse result = itemService.addItem(2L, request(2L, 15));

        assertEquals(15, result.getQuantity());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getItem_shouldReturnItem() {
        Product product = product(2L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, product, 15)));

        assertEquals(15, itemService.getItemById(1L).getQuantity());
    }

    @Test
    void getItem_shouldThrowExceptionWhenNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1L));
    }

    @Test
    void updateItem_shouldUpdateItem() {
        Product oldProduct = product(1L);
        Product newProduct = product(2L);
        Item item = item(1L, oldProduct, 5);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(productRepository.findById(2L)).thenReturn(Optional.of(newProduct));
        when(itemRepository.save(item)).thenReturn(item);

        ItemResponse result = itemService.updateItem(1L, request(2L, 15));

        assertEquals(15, result.getQuantity());
        assertEquals(2L, result.getProductId());
    }

    @Test
    void getItemsByProduct_shouldReturnPage() {
        Product product = product(2L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(itemRepository.findByProduct_Id(2L, pageRequest))
                .thenReturn(new PageImpl<>(List.of(item(1L, product, 15))));

        assertEquals(1, itemService.getItemsByProduct(2L, pageRequest).getTotalElements());
    }

    @Test
    void deleteItem_shouldDeleteItem() {
        Item item = item(1L, product(2L), 15);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L);

        verify(itemRepository).delete(item);
    }

    private ItemRequest request(Long productId, Integer quantity) {
        ItemRequest request = new ItemRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }

    private Product product(Long id) {
        Product product = new Product();
        product.setId(id);
        return product;
    }

    private Item item(Long id, Product product, Integer quantity) {
        Item item = new Item();
        item.setId(id);
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }
}
