package com.appsdeveloperblog.ws.products.service;

import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) {

        String productId = UUID.randomUUID().toString();

        // TODO: persist product details into database table before publishing an event

        ProductCreatedEvent productCreatedEvent =
                new ProductCreatedEvent(
                        productId,
                        productRestModel.getTitle(),
                        productRestModel.getPrice(),
                        productRestModel.getQuantity());

        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);

        future.whenComplete(
                (result, exception) -> {
                    if (exception != null) {
                        logger.error("Failed to send message:" + exception.getMessage());
                    } else {
                        logger.info("Message send successfully: " + result.getRecordMetadata());
                    }
                });

//        future.join();

        return productId;
    }
}
