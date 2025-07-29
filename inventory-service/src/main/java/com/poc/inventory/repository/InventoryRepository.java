package com.poc.inventory.repository;

import com.poc.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Inventory> findByProductId(String productId);
}