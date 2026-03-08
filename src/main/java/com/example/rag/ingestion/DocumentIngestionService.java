package com.example.rag.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentIngestionService implements ResourceLoaderAware {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;
    private final IngestionRecordRepository ingestionRecordRepository;
    private final TokenTextSplitter splitter;
    private ResourcePatternResolver resourcePatternResolver;

    public DocumentIngestionService(VectorStore vectorStore,
                                    IngestionRecordRepository ingestionRecordRepository) {
        this.vectorStore = vectorStore;
        this.ingestionRecordRepository = ingestionRecordRepository;
        this.splitter = new TokenTextSplitter();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

    public void ingestAll() throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:*.pdf");

        if (resources.length == 0) {
            log.info("No PDFs found in resources");
            return;
        }

        for (Resource resource : resources) {
            String filename = resource.getFilename();

            if (ingestionRecordRepository.existsByFilename(filename)) {
                log.info("Skipping already ingested: {}", filename);
                continue;
            }

            log.info("Ingesting: {}", filename);

            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> documents = reader.get();
            List<Document> chunks = splitter.apply(documents);

            vectorStore.add(chunks);

            ingestionRecordRepository.save(new IngestionRecord(filename, chunks.size()));
            log.info("Stored {} chunks from {}", chunks.size(), filename);
        }
    }
}