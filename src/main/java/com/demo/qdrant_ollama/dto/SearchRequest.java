package com.demo.qdrant_ollama.dto;

public record SearchRequest(String query, int maxResults){}
