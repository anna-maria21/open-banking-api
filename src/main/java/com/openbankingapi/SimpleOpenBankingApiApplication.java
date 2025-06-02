package com.openbankingapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(
        info = @Info(
                title = "Simplified Open Banking API",
                version = "1.0",
                description = "API documentation for the Open Banking project"
        )
)
@SpringBootApplication
@EnableTransactionManagement
public class SimpleOpenBankingApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleOpenBankingApiApplication.class, args);
    }
}
