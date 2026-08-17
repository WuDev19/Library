package com.example.bookborrowservice.config;

import com.example.bookborrowservice.constants.StringCommon;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .contact(new Contact()
                                .name("Nguyễn Văn Vũ - Wenwu")
                                .email("nguyenvu19a19@gmail.com")
                        )
                        .title("Digital Library")
                        .description("Hệ thống quản lý thư viện số thông minh")
                        .license(new License().name("Version-1"))
                ).components(new Components()
                        .addSecuritySchemes(
                                StringCommon.SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")
                                        .name(StringCommon.AUTHORIZATION)
                        ));
    }

    @Bean
    public GroupedOpenApi bookGroup() {
        return GroupedOpenApi.builder()
                .group("Book")
                .pathsToMatch("/books/**")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryGroup() {
        return GroupedOpenApi.builder()
                .group("Category")
                .pathsToMatch("/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi borrowGroup() {
        return GroupedOpenApi.builder()
                .group("Borrow")
                .pathsToMatch("/borrows/**")
                .build();
    }

}
