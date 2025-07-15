# Qdrant + Ollama Search Application

This Spring Boot application demonstrates semantic search using Qdrant vector database and AI-powered responses using Ollama with the Llama 3.1 model.

## Features

- **Vector Search**: Uses Qdrant for semantic similarity search
- **AI Integration**: Leverages Ollama with Llama 3.1 for intelligent responses
- **Mock Data**: Includes 15 diverse documents across Technology, Science, Business, Health, and Education
- **REST API**: Simple HTTP endpoints for search functionality

## Prerequisites

- Docker and Docker Compose
- Java 24+
- Ollama with Llama 3.1 model installed

## Setup Instructions

### 1. Start Qdrant

```bash
docker-compose up -d
```

This will start Qdrant on port 6333.

### 2. Install and Start Ollama

First, install Ollama from [https://ollama.ai](https://ollama.ai)

Then pull the Llama 3.1 model:

```bash
ollama pull llama3.1
```

Start Ollama:

```bash
ollama serve
```

### 3. Run the Application

```bash
./gradlew bootRun
```

The application will start on port 8080 and automatically initialize mock data in Qdrant.

## API Endpoints

### Search Documents
```http
POST /api/search
Content-Type: application/json

{
  "query": "What is machine learning?",
  "maxResults": 5
}
```

### Initialize Data (Manual)
```http
POST /api/search/initialize
```

### Health Check
```http
GET /api/search/health
```

## Example Queries and Expected Results

### 1. Technology Query
**Query**: "What is machine learning and how does it work?"

**Expected Response**:
- **AI Answer**: A comprehensive explanation of machine learning, its applications, and how it enables computers to learn from data
- **Similar Documents**: 
  - "Introduction to Machine Learning" (high similarity)
  - "The Future of Cloud Computing" (medium similarity)
  - "Blockchain Technology Explained" (lower similarity)

### 2. Health Query
**Query**: "How can I improve my mental health in the digital age?"

**Expected Response**:
- **AI Answer**: Discussion of digital wellness, managing screen time, maintaining real relationships, and using mental health apps
- **Similar Documents**:
  - "Mental Health in the Digital Age" (high similarity)
  - "Exercise and Physical Fitness" (medium similarity)
  - "Nutrition and Modern Diet" (lower similarity)

### 3. Business Query
**Query**: "What are the best strategies for startup funding?"

**Expected Response**:
- **AI Answer**: Comprehensive overview of funding sources (bootstrapping, angel investors, VC, crowdfunding), requirements, and best practices
- **Similar Documents**:
  - "Startup Funding Strategies" (high similarity)
  - "Digital Transformation in Business" (medium similarity)
  - "Remote Work Best Practices" (lower similarity)

### 4. Science Query
**Query**: "How does quantum computing differ from classical computing?"

**Expected Response**:
- **AI Answer**: Explanation of quantum bits vs classical bits, superposition, entanglement, and applications in cryptography and optimization
- **Similar Documents**:
  - "Quantum Computing Fundamentals" (high similarity)
  - "The Human Genome Project" (medium similarity)
  - "Climate Change and Global Warming" (lower similarity)

### 5. Education Query
**Query**: "What are the benefits of online learning?"

**Expected Response**:
- **AI Answer**: Discussion of accessibility, flexibility, global reach, and various e-learning technologies
- **Similar Documents**:
  - "Online Learning and E-Learning" (high similarity)
  - "Lifelong Learning Strategies" (medium similarity)
  - "STEM Education Importance" (lower similarity)

## Mock Data Categories

The application includes 15 documents across 5 categories:

### Technology (3 documents)
- Introduction to Machine Learning
- The Future of Cloud Computing
- Blockchain Technology Explained

### Science (3 documents)
- Climate Change and Global Warming
- The Human Genome Project
- Quantum Computing Fundamentals

### Business (3 documents)
- Digital Transformation in Business
- Startup Funding Strategies
- Remote Work Best Practices

### Health (3 documents)
- Mental Health in the Digital Age
- Nutrition and Modern Diet
- Exercise and Physical Fitness

### Education (3 documents)
- Online Learning and E-Learning
- STEM Education Importance
- Lifelong Learning Strategies

## Response Format

```json
{
  "query": "What is machine learning?",
  "aiAnswer": "Machine learning is a subset of artificial intelligence that enables computers to learn and improve from experience without being explicitly programmed...",
  "similarDocuments": [
    {
      "id": "tech-001",
      "title": "Introduction to Machine Learning",
      "content": "Machine learning is a subset of artificial intelligence...",
      "category": "Technology",
      "author": "Dr. Sarah Johnson",
      "similarity": 0.95
    }
  ],
  "searchTimeMs": 1250
}
```

## Architecture

1. **Qdrant Vector Store**: Stores document embeddings for semantic search
2. **Ollama Integration**: Provides AI-powered responses using Llama 3.1
3. **Spring AI**: Handles vector operations and AI model interactions
4. **REST API**: Exposes search functionality via HTTP endpoints

## Troubleshooting

### Qdrant Connection Issues
- Ensure Docker is running
- Check if Qdrant is accessible at `http://localhost:6333`
- Verify the collection is created: `curl http://localhost:6333/collections/documents`

### Ollama Issues
- Ensure Ollama is running: `ollama serve`
- Verify Llama 3.1 is installed: `ollama list`
- Check Ollama is accessible at `http://localhost:11434`

### Application Issues
- Check logs for detailed error messages
- Verify all services are running before starting the application
- Use the health endpoint to verify service status

## Development

To modify the mock data, edit the `generateMockDocuments()` method in `MockDataService.java`.

To change the AI model, update the `spring.ai.ollama.chat.model` property in `application.properties`. 