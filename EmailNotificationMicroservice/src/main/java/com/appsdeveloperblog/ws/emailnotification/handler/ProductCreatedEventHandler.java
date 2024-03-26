package com.appsdeveloperblog.ws.emailnotification.handler;

import com.appsdeveloperblog.ws.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductCreatedEventHandler.class);

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        log.info("Received a new event: " + productCreatedEvent.getTitle());
    }
}
