package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ShopOrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopOrderHistoryRepository extends JpaRepository<ShopOrderHistoryEntity, Long> {
    List<ShopOrderHistoryEntity> findByShopOrderId(Long shopOrderId);
}
