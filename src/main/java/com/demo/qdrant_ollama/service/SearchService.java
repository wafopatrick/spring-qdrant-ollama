package com.demo.qdrant_ollama.service;

import com.demo.qdrant_ollama.dto.SearchRequest;
import com.demo.qdrant_ollama.dto.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final VectorStore vectorStore;

    private final OllamaChatModel chatModel;

    private final MockDataService mockDataService;

    @Autowired
    public SearchService(@Qualifier("customVectorStore") VectorStore vectorStore, OllamaChatModel chatModel, MockDataService mockDataService) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.mockDataService = mockDataService;
    }

    public SearchResponse search(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            // Perform similarity search using VectorStore with SearchRequest
            org.springframework.ai.vectorstore.SearchRequest vectorSearchRequest =  org.springframework.ai.vectorstore.SearchRequest.builder().query(request.query())
                    .topK(request.maxResults())
                    .similarityThreshold(0.7) // Adjust the threshold as needed
                    .build();
            List<Document> similarDocuments = Optional.ofNullable(vectorStore.similaritySearch(vectorSearchRequest)).orElse(List.of());

            logger.info("found similarDocuments size: {}", similarDocuments.size());

            // Convert documents to DocumentResult
            List<SearchResponse.DocumentResult> documentResults = similarDocuments.stream()
                    .map(doc -> new SearchResponse.DocumentResult(
                            doc.getId(),
                            doc.getMetadata().getOrDefault("title", "Unknown Title").toString(),
                            doc.getText(),
                            doc.getMetadata().getOrDefault("category", "No Category").toString(),
                            doc.getMetadata().getOrDefault("author", "Unknown Author").toString(),
                            doc.getMetadata().containsKey("similarityScore")
                                    ? ((Float) doc.getMetadata().get("similarityScore")).doubleValue()
                                    : 1.0
                    ))
                    .collect(Collectors.toList());

            // Generate AI response with context
            String aiAnswer = generateAIResponse(request.query(), documentResults);

            long searchTime = System.currentTimeMillis() - startTime;
            return new SearchResponse(request.query(), aiAnswer, documentResults, searchTime);
        } catch (Exception e) {
            logger.error("Error during search: ", e);
            throw new RuntimeException("Search failed: " + e.getMessage());
        }
    }

    private String generateAIResponse(String query, List<SearchResponse.DocumentResult> documents) {
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Based on the following relevant documents, please answer the user's question.\n\n");
        contextBuilder.append("User Question: ").append(query).append("\n\n");
        contextBuilder.append("Relevant Documents:\n");
        for (int i = 0; i < documents.size(); i++) {
            SearchResponse.DocumentResult doc = documents.get(i);
            contextBuilder.append(i + 1).append(". ").append(doc.title()).append(" (by ").append(doc.author()).append(")\n");
            contextBuilder.append("Category: ").append(doc.category()).append("\n");
            contextBuilder.append("Content: ").append(doc.content()).append("\n");
            contextBuilder.append("Similarity Score: ").append(doc.similarity()).append("\n\n");
        }
        contextBuilder.append("Please provide a comprehensive answer based on the information from these documents. " +
                "If the documents don't contain enough information to answer the question, please say so.");

        // Integrate with Ollama via ChatClient
        String prompt = contextBuilder.toString();
        try {
            return this.chatModel.call(prompt);
        } catch (Exception e) {
            logger.error("Error generating AI response: ", e);
            return "Failed to generate AI response: " + e.getMessage();
        }
    }

    public void initializeData() {
        mockDataService.initializeMockData();
    }
}