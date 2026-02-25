package com.gregor_lohaus.gtransfer.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gregor_lohaus.gtransfer.model.File;
import com.gregor_lohaus.gtransfer.model.FileRepository;
import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;

@Component
@ConditionalOnProperty(name = "gtransfer-config.upload.cleanupEnabled", havingValue = "true", matchIfMissing = true)
public class FileCleanupService {

    private static final Logger log = LoggerFactory.getLogger(FileCleanupService.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AbstractStorageService storageService;

    @Scheduled(fixedDelayString = "${gtransfer-config.upload.cleanupIntervalMs:3600000}")
    @Transactional
    public void cleanupExpiredFiles() {
        List<File> expired = fileRepository.findExpired(LocalDateTime.now());
        if (expired.isEmpty()) return;

        for (File file : expired) {
            storageService.delete(file.getId());
            fileRepository.delete(file);
        }

        log.info("Cleaned up {} expired file(s)", expired.size());
    }
}
