package com.example.mdd_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MddBackendApplication {

    private static final Logger log = LoggerFactory.getLogger(
        MddBackendApplication.class
    );

    public static void main(String[] args) {
        log.info("⚠️ Coucou depuis l'application");
        SpringApplication.run(MddBackendApplication.class, args);
    }
}
