package com.example.springaws;

import com.amazonaws.services.s3.event.S3EventNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueListener {

    @Value("${custom.sqs-queue-name}")
    private String queueName;

    private final QueueMessagingTemplate queueMessagingTemplate;

    public QueueListener(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener(value = "${custom.sqs-queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onS3UploadEvent(S3EventNotification event) {
        log.info("Incoming S3EventNotification: " + event.toJson());

        if (event.getRecords() != null && !event.getRecords().isEmpty()) {
            String bucket = event.getRecords().get(0).getS3().getBucket().getName();
            String key = event.getRecords().get(0).getS3().getObject().getKey();

            Message<String> payload = MessageBuilder
                    .withPayload("New upload happened: " + bucket + "/" + key)
                    .build();
            this.queueMessagingTemplate.convertAndSend(queueName, payload);
            log.info("Message {} sent", payload);
        }

    }
}
