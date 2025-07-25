package com.braidsbeautyByAngie;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.braidsbeautyByAngie.*")
@EntityScan("com.braidsbeautyByAngie.*")
@EnableJpaRepositories("com.braidsbeautyByAngie")
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableFeignClients("com.braidsbeautyByAngie.*")
@OpenAPIDefinition
@EnableDiscoveryClient
public class OrdersServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdersServiceApplication.class, args);
    }
}
