package com.aimi.listener;

import com.aimi.entity.StepTwoProduct;
import com.aimi.model.SagaCommand;
import com.aimi.model.SagaEvent;
import com.aimi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class StepTwoCommandListener {

    private static final String TOPIC_LISTEN = "saga.commands";
    private static final String TOPIC_PUBL = "saga.events";
    private static final String GROUP = "step2-group";
    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";
    private static final String COMPENSATED = "COMPENSATED";
    private static final String EXECUTE = "execute";
    private static final String COMPENSATE = "compensate";
    private static final int STEP = 2;

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, SagaEvent> kafkaTemplate;
    private final Random random = new Random();

    @KafkaListener(topics = TOPIC_LISTEN, groupId = GROUP)
    public void handleCommand(SagaCommand command) {
        if (command.getStep() != STEP) {
            return; // игнорируем команды не для этого шага
        }

        try {
            if (EXECUTE.equals(command.getAction())) {
                executeStep(command.getTxId());
            } else if (COMPENSATE.equals(command.getAction())) {
                compensateStep(command.getTxId());
            }
        } catch (Exception e) {
            log.error("Error handling command for tx {}: {}", command.getTxId(), e.getMessage(), e);
            publishEvent(command.getTxId(), FAILED, e.getMessage());
        }
    }

    private void executeStep(String txId) {
        // Имитация сбоя: каждый 3-й вызов падает
        if (random.nextInt(3) == 0) {
            throw new RuntimeException("Simulated failure in Step " + STEP);
        }

        productRepository.save(new StepTwoProduct(txId));
        log.info("Step {} executed for tx {}", STEP, txId);
        publishEvent(txId, COMPLETED, null);
    }

    private void compensateStep(String txId) {
        productRepository.deleteById(txId);
        log.info("Step {} compensated for tx {}", STEP, txId);
        publishEvent(txId, COMPENSATED, null);
    }

    private void publishEvent(String txId, String status, String message) {
        SagaEvent event = new SagaEvent();
        event.setTxId(txId);
        event.setStep(STEP);
        event.setStatus(status);
        event.setDescription(message);
        kafkaTemplate.send(TOPIC_PUBL, txId, event);
    }
}