package com.example.bookborrowservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BookBorrowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookBorrowServiceApplication.class, args);
    }

}
