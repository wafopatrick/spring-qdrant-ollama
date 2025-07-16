package com.demo.qdrant_ollama.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class VectorStoreConfig {

    @Value("${vector-store.provider:qdrant}")
    private String vectorStoreProvider;

    @Bean(name = "customVectorStore")
    public VectorStore vectorStore(QdrantClient qdrantClient, MilvusServiceClient milvusClient, EmbeddingModel embeddingModel) {
        if ("milvus".equalsIgnoreCase(vectorStoreProvider)) {
            // Initialize Milvus collection before creating the vector store
            initializeMilvusCollection(milvusClient);
            
            return MilvusVectorStore.builder(milvusClient, embeddingModel)
                    .embeddingDimension(1024) // Adjust the dimension based on your embedding model
                    .collectionName("documents")
                    .databaseName("default")
                    .indexType(IndexType.IVF_FLAT)
                    .metricType(MetricType.COSINE)
                    .initializeSchema(true)
                    .build();
        } else {
            return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                    .collectionName("documents")
                    .initializeSchema(true)
                    .build();
        }
    }

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder grpcClientBuilder =
                QdrantGrpcClient.newBuilder(
                        "localhost",
                        6334,
            false);

        return new QdrantClient(grpcClientBuilder.build());
    }

    @Bean
    public MilvusServiceClient milvusClient() {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization("minioadmin", "minioadmin")
                .withUri("http://localhost:19530")
                .build());
    }

    @Bean
    public EmbeddingModel embeddingModel(OllamaApi ollamaApi) {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .modelManagementOptions(
                        ModelManagementOptions.builder()
                                .pullModelStrategy(PullModelStrategy.WHEN_MISSING)
                                .build()
                )
                .defaultOptions(OllamaOptions.builder().model("mxbai-embed-large").build())
                .build();
    }

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi.Builder().build(); // Uses default base URL (http://localhost:11434)
        // Or specify a custom base URL if needed:
        // return new OllamaApi.Builder().baseUrl("http://your-ollama-server-host:port").build();
    }

    private void initializeMilvusCollection(MilvusServiceClient client) {
        String collectionName = "documents";
        String vectorFieldName = "embedding";
        int dimension = 1024; // match your Ollama embeddings
        MetricType metricType = MetricType.L2;

        try {
            // 1. Create collection if it doesn't exist
            if (!client.hasCollection(HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build()).getData()) {

                // Define fields using CollectionSchemaParam
                CollectionSchemaParam schema = CollectionSchemaParam.newBuilder()
                        .withFieldTypes(
                                List.of(
                                        FieldType.newBuilder()
                                                .withName("id")
                                                .withDataType(DataType.Int64)
                                                .withPrimaryKey(true)
                                                .withAutoID(true)
                                                .build(),
                                        FieldType.newBuilder()
                                                .withName(vectorFieldName)
                                                .withDataType(DataType.FloatVector)
                                                .withDimension(dimension)
                                                .build()
                                )
                        )
                        .build();

                client.createCollection(CreateCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withDescription("Collection for document embeddings")
                        .withSchema(schema)
                        .withShardsNum(2)
                        .build());
            }

            // 2. Create index on the vector field only if it doesn't exist
            String indexName = "embedding_index";
            try {
                // Check if index already exists
                boolean indexExists = client.describeIndex(DescribeIndexParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withFieldName(indexName)
                        .build()).getData().getIndexDescriptionsCount() > 0;
                
                if (!indexExists) {
                    client.createIndex(CreateIndexParam.newBuilder()
                            .withCollectionName(collectionName)
                            .withFieldName(vectorFieldName)
                            .withIndexName(indexName)
                            .withIndexType(IndexType.IVF_FLAT) // good balance of speed + accuracy
                            .withMetricType(metricType)
                            .withExtraParam("{\"nlist\":128}") // tune for data size
                            .withSyncMode(true)
                            .build());
                }
            } catch (Exception e) {
                // If describeIndex fails, try to create the index anyway
                System.err.println("Could not check if index exists, attempting to create: " + e.getMessage());
                client.createIndex(CreateIndexParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withFieldName(vectorFieldName)
                        .withIndexName(indexName)
                        .withIndexType(IndexType.IVF_FLAT)
                        .withMetricType(metricType)
                        .withExtraParam("{\"nlist\":128}")
                        .withSyncMode(true)
                        .build());
            }

            // 3. Load collection into memory
            client.loadCollection(LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build());
        } catch (Exception e) {
            // Log the error but don't fail the application startup
            System.err.println("Failed to initialize Milvus collection: " + e.getMessage());
        }
    }
}
