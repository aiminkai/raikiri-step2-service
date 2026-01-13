package com.aimi.model;


import lombok.Data;

@Data
public class SagaCommand {
    private String txId;
    private int step;
    private String action; // "execute", "compensate"
    private Integer value;
}