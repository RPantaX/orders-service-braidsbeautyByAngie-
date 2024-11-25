package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ShopOrderEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOrderRepository extends JpaRepository<ShopOrderEntity, Long> {

}
