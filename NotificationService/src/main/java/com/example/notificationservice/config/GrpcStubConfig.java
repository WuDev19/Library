package com.example.notificationservice.config;

import com.example.grpc.user.v1.UserServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.util.List;

@Configuration
public class GrpcStubConfig {

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(
            GrpcChannelFactory factory,
            GrpcJwtInterceptor jwtInterceptor
    ) {
        ChannelBuilderOptions options = ChannelBuilderOptions.defaults()
                .withInterceptors(List.of(jwtInterceptor))
                .withInterceptorsMerge(true);
        ManagedChannel channel = factory.createChannel("user-service", options);
        return UserServiceGrpc.newBlockingStub(channel);
    }
}
