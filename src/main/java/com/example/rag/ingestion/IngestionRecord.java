package com.example.rag.ingestion;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingestion_records")
public class IngestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String filename;

    private int chunkCount;

    private LocalDateTime ingestedAt;

    public IngestionRecord() {}

    public IngestionRecord(String filename, int chunkCount) {
        this.filename = filename;
        this.chunkCount = chunkCount;
        this.ingestedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public int getChunkCount() { return chunkCount; }
    public LocalDateTime getIngestedAt() { return ingestedAt; }
}