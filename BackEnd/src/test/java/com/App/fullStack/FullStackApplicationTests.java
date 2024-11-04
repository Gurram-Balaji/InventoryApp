package com.App.fullStack;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FullStackApplicationTests {

	// Test to verify if the application context loads successfully
	@Test
	void contextLoads() {
		// This test will pass if the application context loads successfully
	}
	@Test
	void applicationStarts() {
		assertDoesNotThrow(() -> SpringApplication.run(FullStackApplication.class));
	}
}
