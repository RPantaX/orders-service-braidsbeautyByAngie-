package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address_entity")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false)
    private Long addressId;
    @Column(name = "address_street", nullable = false)
    private String addressStreet;
    @Column(name = "address_city", nullable = false)
    private String addressCity;
    @Column(name = "address_state", nullable = false)
    private String addressState;
    @Column(name = "address_postal_code", nullable = true)
    private String addressPostalCode;
    @Column(name = "address_country", nullable = false)
    private String addressCountry;
}
