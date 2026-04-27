package com.telemed.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        System.out.println("/api/status");
        HashMap<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Backend is running successfully!");
        return response;
    }

}
