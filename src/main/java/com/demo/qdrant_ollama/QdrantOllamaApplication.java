package com.demo.qdrant_ollama;

import org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication( exclude = {
		QdrantVectorStoreAutoConfiguration.class,
		MilvusVectorStoreAutoConfiguration.class
})
public class QdrantOllamaApplication {

	public static void main(String[] args) {
		SpringApplication.run(QdrantOllamaApplication.class, args);
	}

}
