package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ShopOrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOrderHistoryRepository extends JpaRepository<ShopOrderHistoryEntity, Long> {
}
