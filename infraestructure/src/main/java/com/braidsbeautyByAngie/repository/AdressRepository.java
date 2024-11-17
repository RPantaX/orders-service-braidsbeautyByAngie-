package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdressRepository  extends JpaRepository<AddressEntity, Long> {
}
