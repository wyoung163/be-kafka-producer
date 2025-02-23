package com.example.oliveyoungbe.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	@GetMapping("/health/v1")
	public Map<String, String> healthCheck() {
		Map<String, String> response = new HashMap<>();
		response.put("status", "OK");

		return response;
	}
}
