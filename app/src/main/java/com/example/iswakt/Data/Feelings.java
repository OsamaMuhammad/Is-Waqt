package com.example.iswakt.Data;

public class Feelings {
    private String documentId;
    private int state;

    public Feelings(String documentId, int state) {
        this.documentId = documentId;
        this.state = state;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getState() {
        return state;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setState(int state) {
        this.state = state;
    }
}
