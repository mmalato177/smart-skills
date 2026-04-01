package com.smart_skills.dto;

public class ImportResultDTO {

    private int created;
    private int updated;
    private int skipped;

    public ImportResultDTO(int created, int updated, int skipped) {
        this.created = created;
        this.updated = updated;
        this.skipped = skipped;
    }

    public int getCreated() {
        return created;
    }

    public int getUpdated() {
        return updated;
    }

    public int getSkipped() {
        return skipped;
    }
}

