package com.openbankingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SimpleOpenBankingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleOpenBankingApiApplication.class, args);
    }

}
