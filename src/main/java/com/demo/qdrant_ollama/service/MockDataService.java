package com.demo.qdrant_ollama.service;

import com.demo.qdrant_ollama.model.Document;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class MockDataService {

    private static final Logger logger = LoggerFactory.getLogger(MockDataService.class);

    private final QdrantVectorStore vectorStore;

    @Autowired
    public MockDataService(QdrantVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void initializeMockData() {
        logger.info("Initializing mock data in Qdrant...");

        // avoid adding duplicate documents by deleting existing ones
        deleteAllDocuments();

        List<Document> documents = generateMockDocuments();
        List<org.springframework.ai.document.Document> aiDocuments = convertToAIDocuments(documents);

        vectorStore.add(aiDocuments);

        logger.info("Successfully added {} documents to Qdrant", documents.size());
    }

    private void deleteAllDocuments() {
        String collectionName = "documents";
        logger.info("Deleting all existing documents from collection '{}'...", collectionName);
        Optional<QdrantClient> nativeClient = vectorStore.getNativeClient();
        if (nativeClient.isPresent()) {
            QdrantClient qdrantClient = nativeClient.get();
            try {
                // create an empty filter to match all documents
                Points.Filter filter = Points.Filter.newBuilder().build();

                qdrantClient.deleteAsync(
                        Points.DeletePoints.newBuilder()
                                .setCollectionName(collectionName)
                                .setPoints(Points.PointsSelector.newBuilder().setFilter(filter).build())
                                .build()
                ).get();
                logger.info("Successfully deleted all documents from collection '{}'", collectionName);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Failed to delete document from collection '{}'", collectionName, e);
            }
        } else {
            logger.error("QdrantClient not available. Cannot delete documents from collection '{}'", collectionName);
        }
    }

    private List<Document> generateMockDocuments() {
        List<Document> documents = new ArrayList<>();

        // Technology Articles
        documents.add(new Document("tech-001", "Introduction to Machine Learning",
                "Machine learning is a subset of artificial intelligence that enables computers to learn and improve from experience without being explicitly programmed. " +
                        "It involves algorithms that can identify patterns in data and make predictions or decisions based on those patterns. " +
                        "Common applications include recommendation systems, image recognition, natural language processing, and autonomous vehicles. " +
                        "The field has seen tremendous growth in recent years due to the availability of large datasets and increased computational power.",
                "Technology", "Dr. Sarah Johnson", "machine-learning, AI, algorithms"));

        documents.add(new Document("tech-002", "The Future of Cloud Computing",
                "Cloud computing has revolutionized how businesses operate by providing scalable, on-demand computing resources. " +
                        "Platforms like AWS, Azure, and Google Cloud offer services ranging from virtual machines to serverless functions. " +
                        "The shift to cloud-native architectures has enabled faster development cycles and improved scalability. " +
                        "Edge computing and hybrid cloud solutions are emerging trends that complement traditional cloud services.",
                "Technology", "Mike Chen", "cloud-computing, AWS, scalability"));

        documents.add(new Document("tech-003", "Blockchain Technology Explained",
                "Blockchain is a distributed ledger technology that maintains a continuously growing list of records, called blocks, " +
                        "which are linked and secured using cryptography. Each block contains a cryptographic hash of the previous block, " +
                        "a timestamp, and transaction data. This design makes blockchain resistant to modification of data. " +
                        "Beyond cryptocurrencies, blockchain has applications in supply chain management, voting systems, and digital identity.",
                "Technology", "Alex Rodriguez", "blockchain, cryptocurrency, distributed-ledger"));

        // Science Articles
        documents.add(new Document("sci-001", "Climate Change and Global Warming",
                "Climate change refers to long-term shifts in global weather patterns and average temperatures. " +
                        "The primary driver of recent climate change is the increase in greenhouse gases, particularly carbon dioxide, " +
                        "released through human activities such as burning fossil fuels and deforestation. " +
                        "Effects include rising sea levels, more frequent extreme weather events, and shifts in ecosystems. " +
                        "Addressing climate change requires global cooperation and transition to renewable energy sources.",
                "Science", "Dr. Emily Watson", "climate-change, global-warming, environment"));

        documents.add(new Document("sci-002", "The Human Genome Project",
                "The Human Genome Project was an international research effort to determine the sequence of the human genome " +
                        "and identify the genes that it contains. Completed in 2003, it mapped approximately 20,000-25,000 genes. " +
                        "This project has revolutionized medicine by enabling personalized treatments and understanding genetic diseases. " +
                        "It has also led to advances in biotechnology and our understanding of human evolution.",
                "Science", "Dr. Robert Kim", "genetics, human-genome, medicine"));

        documents.add(new Document("sci-003", "Quantum Computing Fundamentals",
                "Quantum computing uses quantum mechanical phenomena such as superposition and entanglement to process information. " +
                        "Unlike classical computers that use bits (0 or 1), quantum computers use quantum bits or qubits that can exist " +
                        "in multiple states simultaneously. This enables quantum computers to solve certain problems exponentially faster " +
                        "than classical computers, particularly in cryptography, optimization, and drug discovery.",
                "Science", "Dr. Lisa Park", "quantum-computing, physics, cryptography"));

        // Business Articles
        documents.add(new Document("bus-001", "Digital Transformation in Business",
                "Digital transformation is the integration of digital technology into all areas of a business, fundamentally changing " +
                        "how organizations operate and deliver value to customers. It involves adopting new technologies like cloud computing, " +
                        "artificial intelligence, and the Internet of Things. Companies undergoing digital transformation often see improved " +
                        "efficiency, better customer experiences, and new business models. However, it requires cultural change and upskilling employees.",
                "Business", "Jennifer Smith", "digital-transformation, business-strategy, technology"));

        documents.add(new Document("bus-002", "Startup Funding Strategies",
                "Startup funding is crucial for turning innovative ideas into successful businesses. Common funding sources include " +
                        "bootstrapping, angel investors, venture capital, and crowdfunding. Each funding stage has different requirements " +
                        "and expectations. Successful fundraising requires a compelling pitch, solid business plan, and strong team. " +
                        "Understanding valuation, equity distribution, and investor relations is essential for long-term success.",
                "Business", "David Wilson", "startup, funding, venture-capital"));

        documents.add(new Document("bus-003", "Remote Work Best Practices",
                "Remote work has become increasingly popular, especially after the COVID-19 pandemic. Effective remote work requires " +
                        "proper communication tools, clear expectations, and trust between team members. Companies need to establish " +
                        "remote work policies, provide necessary technology, and maintain team cohesion. Remote work offers benefits " +
                        "like increased flexibility and reduced commuting, but also presents challenges in collaboration and work-life balance.",
                "Business", "Maria Garcia", "remote-work, workplace, productivity"));

        // Health Articles
        documents.add(new Document("health-001", "Mental Health in the Digital Age",
                "The digital age has brought both opportunities and challenges for mental health. Social media can provide " +
                        "support networks and access to mental health resources, but also contribute to anxiety, depression, and " +
                        "cyberbullying. Digital wellness involves managing screen time, maintaining real-world relationships, and " +
                        "using technology mindfully. Mental health apps and teletherapy have made mental health care more accessible.",
                "Health", "Dr. James Thompson", "mental-health, digital-wellness, social-media"));

        documents.add(new Document("health-002", "Nutrition and Modern Diet",
                "Modern nutrition science emphasizes the importance of a balanced diet rich in whole foods, fruits, vegetables, " +
                        "and lean proteins. Processed foods, high in sugar and unhealthy fats, contribute to various health problems. " +
                        "Personalized nutrition, based on genetics and microbiome, is an emerging field. Sustainable eating practices " +
                        "that consider environmental impact are also gaining importance in dietary choices.",
                "Health", "Dr. Amanda Lee", "nutrition, diet, health"));

        documents.add(new Document("health-003", "Exercise and Physical Fitness",
                "Regular physical activity is essential for maintaining good health and preventing chronic diseases. " +
                        "The World Health Organization recommends at least 150 minutes of moderate-intensity exercise per week. " +
                        "Exercise benefits include improved cardiovascular health, stronger muscles and bones, better mental health, " +
                        "and weight management. Finding enjoyable activities and building consistent habits is key to long-term fitness.",
                "Health", "Dr. Carlos Martinez", "exercise, fitness, health"));

        // Education Articles
        documents.add(new Document("edu-001", "Online Learning and E-Learning",
                "Online learning has transformed education by making it more accessible and flexible. Platforms like Coursera, " +
                        "edX, and Udemy offer courses from top universities worldwide. E-learning technologies include video lectures, " +
                        "interactive quizzes, discussion forums, and virtual reality simulations. The COVID-19 pandemic accelerated " +
                        "adoption of online learning, highlighting both its benefits and challenges in maintaining student engagement.",
                "Education", "Prof. Sarah Williams", "online-learning, education, technology"));

        documents.add(new Document("edu-002", "STEM Education Importance",
                "STEM (Science, Technology, Engineering, and Mathematics) education is crucial for preparing students " +
                        "for the modern workforce. It develops critical thinking, problem-solving, and analytical skills. " +
                        "STEM careers are among the fastest-growing and highest-paying jobs. Encouraging diversity in STEM fields " +
                        "is important for innovation and addressing complex global challenges. Hands-on projects and real-world " +
                        "applications make STEM subjects more engaging for students.",
                "Education", "Prof. Michael Brown", "STEM, education, careers"));

        documents.add(new Document("edu-003", "Lifelong Learning Strategies",
                "Lifelong learning is the continuous pursuit of knowledge and skills throughout one's life. In today's " +
                        "rapidly changing world, continuous learning is essential for career advancement and personal growth. " +
                        "Effective lifelong learning involves setting clear goals, using various learning methods, and staying " +
                        "curious and open-minded. Online courses, workshops, reading, and networking are valuable learning resources.",
                "Education", "Prof. Lisa Anderson", "lifelong-learning, personal-development, education"));

        return documents;
    }

    private List<org.springframework.ai.document.Document> convertToAIDocuments(List<Document> documents) {
        List<org.springframework.ai.document.Document> aiDocuments = new ArrayList<>();

        for (Document doc : documents) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", doc.title());
            metadata.put("category", doc.category());
            metadata.put("author", doc.author());
            metadata.put("tags", doc.tags());
            metadata.put("createdAt", doc.createdAt().toString());

            org.springframework.ai.document.Document aiDoc = new org.springframework.ai.document.Document(doc.content(), metadata);
            aiDocuments.add(aiDoc);
        }

        return aiDocuments;
    }
}
