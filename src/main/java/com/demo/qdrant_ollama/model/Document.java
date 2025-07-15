package com.demo.qdrant_ollama.model;

import java.time.LocalDateTime;

public record Document(
    String id,
    String title,
    String content,
    String category,
    LocalDateTime createdAt,
    String author,
    String tags
) {
    // Hauptkonstruktor mit generierten createdAt
    public Document(String id, String title, String content, String category, String author, String tags) {
        this(id, title, content, category, LocalDateTime.now(), author, tags);
    }
}
