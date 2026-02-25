package com.gregor_lohaus.gtransfer.services.filecleanup;

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

public class FileCleanupService {
    private Boolean enabled;
    public FileCleanupService(Boolean enabled) {
        this.enabled = enabled;
    }
    private static final Logger log = LoggerFactory.getLogger(FileCleanupService.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AbstractStorageService storageService;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void cleanupExpiredFiles() {
        log.info("Cleaneup started");
        if (!enabled) {
            log.info("Cleaneup skipped");
            return;            
        }
        List<File> expired = fileRepository.findExpired(LocalDateTime.now());
        if (expired.isEmpty()) {
            log.info("Nothing to clean up");
            return;
        };

        for (File file : expired) {
            storageService.delete(file.getId());
            fileRepository.delete(file);
        }
        log.info("Cleaned up {} expired file(s)", expired.size());
    }
}
