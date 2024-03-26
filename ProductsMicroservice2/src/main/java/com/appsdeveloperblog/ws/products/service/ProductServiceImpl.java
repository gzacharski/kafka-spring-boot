package com.appsdeveloperblog.ws.products.service;

import com.appsdeveloperblog.ws.ProductCreatedEvent;
import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel)
            throws ExecutionException, InterruptedException {

        String productId = UUID.randomUUID().toString();

        // TODO: persist product details into database table before publishing an event

        ProductCreatedEvent productCreatedEvent =
                new ProductCreatedEvent(
                        productId,
                        productRestModel.getTitle(),
                        productRestModel.getPrice(),
                        productRestModel.getQuantity());

        logger.info("Before publishing a ProductCreatedEvent");

        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate
                        .send("test-topic2", productId, productCreatedEvent)
                        .get();

        logger.info("Partition: " + result.getRecordMetadata().partition());
        logger.info("Topic: " + result.getRecordMetadata().topic());
        logger.info("Offset: " + result.getRecordMetadata().offset());

        return productId;
    }
}
