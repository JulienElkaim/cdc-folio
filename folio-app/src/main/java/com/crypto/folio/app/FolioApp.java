package com.crypto.folio.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.crypto.ref.data.**")
public class FolioApp {

    public static void main(String[] args) {
        SpringApplication.run(FolioApp.class, args);
    }
}

