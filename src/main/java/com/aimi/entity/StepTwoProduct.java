package com.aimi.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "step_two_products")
@Data
public class StepTwoProduct {
    @Id
    private String transactionId;
    private String productId = "PROD-123";

    public StepTwoProduct(String transactionId) {
        this.transactionId = transactionId;
    }

    public StepTwoProduct() {
    }
}
