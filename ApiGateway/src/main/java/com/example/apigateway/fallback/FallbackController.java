package com.example.apigateway.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class FallbackController {

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String NOT_AVAILABLE = "Server hiện tại không khả dụng";

    @RequestMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallback(@RequestParam String service) {
        log.debug("Service bị lỗi {}", service);
        return ResponseEntity.ok(Map.of(
                CODE, 503,
                MESSAGE, NOT_AVAILABLE
        ));
    }

}
