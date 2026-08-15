package com.example.notificationservice.dto.common;

import java.util.Map;

public class CRUDResponseHelper {
    public static Map<String, Object> updateSuccess() {
        return Map.of("updated", true);
    }
}
