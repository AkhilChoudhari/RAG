package com.example.rag.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionRecordRepository extends JpaRepository<IngestionRecord, Long> {
    boolean existsByFilename(String filename);
}