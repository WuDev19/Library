package com.example.notificationservice;

import com.example.grpc.user.v1.UserServiceGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ImportGrpcClients(
        types = UserServiceGrpc.UserServiceBlockingStub.class,
        target = "user-service"
)
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}