package com.demo.qdrant_ollama.config;

import com.demo.qdrant_ollama.service.MockDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    
    private final MockDataService mockDataService;

    @Autowired
    public ApplicationConfig(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            logger.info("Initializing application data...");
            try {
                // Wait a bit for Qdrant to be ready
                Thread.sleep(5000);
                mockDataService.initializeMockData();
                logger.info("Application data initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize data: ", e);
            }
        };
    }
}