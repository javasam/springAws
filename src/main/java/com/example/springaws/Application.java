package com.example.springaws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = ContextInstanceDataAutoConfiguration.class)
public class Application {

    private final AmazonS3Client amazonS3Client;

    public Application(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(classes = ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        for (Bucket availableBuckets : amazonS3Client.listBuckets()) {
            System.out.println(availableBuckets.getName());
        }
    }
}
