package com.demo.qdrant_ollama.controller;

import com.demo.qdrant_ollama.dto.SearchRequest;
import com.demo.qdrant_ollama.dto.SearchResponse;
import com.demo.qdrant_ollama.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request) {
        logger.info("Received search request: {}", request.query());
        
        try {
            SearchResponse response = searchService.search(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing search request: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Search service is running");
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeData() {
        try {
            searchService.initializeData();
            return ResponseEntity.ok("Data initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing data: ", e);
            return ResponseEntity.internalServerError().body("Failed to initialize data: " + e.getMessage());
        }
    }
} 