package com.example.authservice.config;

import com.example.authservice.entity.Role;
import com.example.authservice.service.base.IJwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class FeignJwtInterceptor implements RequestInterceptor {

    private final IJwtService jwtService;

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && !authHeader.isBlank()) {
                template.header("Authorization", authHeader);
                return;
            }
        }

        Role systemRole = Role.builder().roleName("SYSTEM").build();
        String systemToken = jwtService.generateToken("system_service", 0L, Set.of(systemRole));
        template.header("Authorization", "Bearer " + systemToken);
    }
}
