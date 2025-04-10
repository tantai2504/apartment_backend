package com.example.apartmentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApartmentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApartmentManagementApplication.class, args);
    }

}
