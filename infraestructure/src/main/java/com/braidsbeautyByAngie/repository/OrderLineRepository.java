package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
}
