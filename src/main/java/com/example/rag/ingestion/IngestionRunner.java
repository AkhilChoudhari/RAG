package com.example.rag.ingestion;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class IngestionRunner implements ApplicationRunner {

    private final DocumentIngestionService documentIngestionService;

    public IngestionRunner(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        documentIngestionService.ingestAll();
    }
}
