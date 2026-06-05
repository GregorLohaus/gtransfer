package com.gregor_lohaus.gtransfer.services.filewriter;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gregor_lohaus.gtransfer.config.types.StorageServiceType;

@Configuration
public class StorageServiceConfiguration {

    @Bean
    public AbstractStorageService storageService(
            @Value("${gtransfer-config.storageService.type}") StorageServiceType type,
            @Value("${gtransfer-config.storageService.root}") String root,
            @Value("${gtransfer-config.storageService.bucket:}") String bucket,
            @Value("${gtransfer-config.storageService.region:us-east-1}") String region,
            @Value("${gtransfer-config.storageService.endpoint:}") String endpoint,
            @Value("${gtransfer-config.storageService.accessKeyId:}") String accessKeyId,
            @Value("${gtransfer-config.storageService.secretAccessKey:}") String secretAccessKey,
            @Value("${gtransfer-config.storageService.pathStyleAccessEnabled:false}") boolean pathStyleAccessEnabled) {
        return switch (type) {
            case LOCAL -> new LocalStorageService(Path.of(root));
            case DUMMY -> new DummyStorageService(Path.of(root));
            case S3 -> new S3StorageService(
                    bucket,
                    region,
                    root,
                    endpoint,
                    accessKeyId,
                    secretAccessKey,
                    pathStyleAccessEnabled);
        };
    }
}
