package com.zest.productapi.repository;

import com.zest.productapi.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    Page<Item> findByProduct_Id(Long productId, Pageable pageable);
}
