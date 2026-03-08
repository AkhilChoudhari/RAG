package com.example.rag.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final String PROMPT_TEMPLATE = """
            You are a helpful assistant. Answer the question using only the context provided below.
            If the answer is not in the context, say "I don't have enough information to answer that."

            Context:
            {context}

            Question: {question}
            """;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String answer(String query) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(4)
                        .build()
        );

        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        return chatClient.prompt()
                .user(u -> u.text(PROMPT_TEMPLATE)
                        .param("context", context)
                        .param("question", query))
                .call()
                .content();
    }
}