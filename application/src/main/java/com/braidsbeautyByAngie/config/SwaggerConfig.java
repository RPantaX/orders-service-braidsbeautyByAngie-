package com.braidsbeautyByAngie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("APIs of the 'Orders Service' for AngieBraidsBeauty Microservice")
                        .description("This API provides endpoints for managing orders, Factura, and orderpayment.")
                        .version("v1")
                        .contact(new Contact()
                                .name("PantaX Support")
                                .email("pantajefferson173@gmail.com")
                                .url("https://jefferson-panta.netlify.app/"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
