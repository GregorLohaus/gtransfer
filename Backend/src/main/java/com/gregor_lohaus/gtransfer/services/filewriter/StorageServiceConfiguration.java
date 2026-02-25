package com.gregor_lohaus.gtransfer.services.filewriter;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.gregor_lohaus.gtransfer.config.types.StorageServiceType;

@Configuration
@EnableScheduling
public class StorageServiceConfiguration {

    //TODO S3 implementation
    @Bean
    public AbstractStorageService storageService(
            @Value("${gtransfer-config.storageService.type}") StorageServiceType type,
            @Value("${gtransfer-config.storageService.root}") String root) {
        return switch (type) {
            case LOCAL -> new LocalStorageService(Path.of(root));
            case DUMMY -> new DummyStorageService(Path.of(root));
            case S3 -> new LocalStorageService(Path.of(root));
        };
    }
}
