#!/bin/bash

echo "🚀 Starting Qdrant + Ollama Search Application"
echo "=============================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Ollama is installed
if ! command -v ollama &> /dev/null; then
    echo "❌ Ollama is not installed. Please install Ollama from https://ollama.ai"
    exit 1
fi

# Start Qdrant
echo "📦 Starting Qdrant..."
docker-compose up -d

# Wait for Qdrant to be ready
echo "⏳ Waiting for Qdrant to be ready..."
sleep 10

# Check if Llama 3.1 is available
echo "🤖 Checking Ollama models..."
if ! ollama list | grep -q "llama3.1"; then
    echo "📥 Pulling Llama 3.1 model (this may take a while)..."
    ollama pull llama3.1
else
    echo "✅ Llama 3.1 model is already available"
fi

# Start Ollama if not running
if ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo "🚀 Starting Ollama..."
    ollama serve &
    sleep 5
else
    echo "✅ Ollama is already running"
fi

# Build and run the application
echo "🔨 Building and starting the Spring Boot application..."
./gradlew bootRun

echo "✅ Application setup complete!"
echo ""
echo "🌐 Access the web interface at: http://localhost:8080"
echo "📚 API documentation available in README.md"
echo "🧪 Run test queries with: ./test_queries.sh" 