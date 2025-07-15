#!/bin/bash

# Test script for Qdrant + Ollama Search Application
# Make sure the application is running on port 8080

BASE_URL="http://localhost:8080/api/search"

echo "Testing Qdrant + Ollama Search Application"
echo "=========================================="

# Test 1: Technology Query
echo -e "\n1. Testing Technology Query..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What is machine learning and how does it work?",
    "maxResults": 3
  }' | jq '.'

# Test 2: Health Query
echo -e "\n2. Testing Health Query..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "How can I improve my mental health in the digital age?",
    "maxResults": 3
  }' | jq '.'

# Test 3: Business Query
echo -e "\n3. Testing Business Query..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What are the best strategies for startup funding?",
    "maxResults": 3
  }' | jq '.'

# Test 4: Science Query
echo -e "\n4. Testing Science Query..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "How does quantum computing differ from classical computing?",
    "maxResults": 3
  }' | jq '.'

# Test 5: Education Query
echo -e "\n5. Testing Education Query..."
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What are the benefits of online learning?",
    "maxResults": 3
  }' | jq '.'

echo -e "\nTests completed!" 