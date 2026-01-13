package com.aimi.model;


import lombok.Data;

@Data
public class SagaEvent {
    private String txId;
    private int step;
    private String status; // "COMPLETED", "FAILED"
    private String description;
}