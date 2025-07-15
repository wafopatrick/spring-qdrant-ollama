package com.demo.qdrant_ollama.dto;

import java.util.List;

public record SearchResponse(String query, String aiAnswer, List<DocumentResult> similarDocuments, long searchTimeMs) {

    // Statischer innerer Record für DocumentResult
    public record DocumentResult(String id, String title, String content, String category, String author, double similarity) {}
}
