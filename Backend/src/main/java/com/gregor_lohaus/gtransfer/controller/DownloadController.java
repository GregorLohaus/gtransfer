package com.gregor_lohaus.gtransfer.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gregor_lohaus.gtransfer.model.File;
import com.gregor_lohaus.gtransfer.model.FileRepository;
import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;
import com.gregor_lohaus.gtransfer.services.filewriter.StorageKeys;

@Controller
public class DownloadController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AbstractStorageService storageService;

    @GetMapping("/download")
    public String page() {
        return "download/page";
    }

    @GetMapping("/download/{id}/metadata")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> metadata(@PathVariable String id) {
        AvailableFile available = getAvailableFile(id);
        if (!available.found()) {
            return ResponseEntity.status(available.status()).build();
        }

        File file = available.file();
        return ResponseEntity.ok(Map.of(
                "name", file.getName(),
                "chunkCount", file.getChunkCount()));
    }

    @GetMapping("/download/{id}/chunk/{index}")
    @ResponseBody
    @Transactional
    public ResponseEntity<byte[]> chunk(@PathVariable String id, @PathVariable Integer index) {
        AvailableFile available = getAvailableFile(id);
        if (!available.found()) {
            return ResponseEntity.status(available.status()).build();
        }

        File file = available.file();
        if (index == null || index < 0 || index >= file.getChunkCount()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<byte[]> data = storageService.get(StorageKeys.chunk(file.getId(), index));
        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data.get());
    }

    @PostMapping("/download/{id}/complete")
    @ResponseBody
    @Transactional
    public ResponseEntity<Void> complete(@PathVariable String id) {
        AvailableFile available = getAvailableFile(id);
        if (!available.found()) {
            return ResponseEntity.status(available.status()).build();
        }

        File file = available.file();
        file.setDownloads(file.getDownloads() + 1);
        fileRepository.save(file);

        if (file.getDownloadLimit() != null && file.getDownloads() >= file.getDownloadLimit()) {
            deleteStoredFile(file);
            fileRepository.delete(file);
        }

        return ResponseEntity.noContent().build();
    }

    private AvailableFile getAvailableFile(String id) {
        Optional<File> fileOpt = fileRepository.findById(id);
        if (fileOpt.isEmpty()) {
            return AvailableFile.notFound();
        }

        File file = fileOpt.get();
        if (!file.isCompleted()) {
            return AvailableFile.notFound();
        }

        if (file.getExpireyDateTime() != null && LocalDateTime.now().isAfter(file.getExpireyDateTime())) {
            deleteStoredFile(file);
            fileRepository.delete(file);
            return AvailableFile.gone();
        }

        if (file.getDownloadLimit() != null && file.getDownloads() >= file.getDownloadLimit()) {
            deleteStoredFile(file);
            fileRepository.delete(file);
            return AvailableFile.gone();
        }

        return AvailableFile.ok(file);
    }

    private void deleteStoredFile(File file) {
        for (int i = 0; i < file.getChunkCount(); i++) {
            storageService.delete(StorageKeys.chunk(file.getId(), i));
        }
    }

    private record AvailableFile(File file, HttpStatus status) {
        static AvailableFile ok(File file) {
            return new AvailableFile(file, HttpStatus.OK);
        }

        static AvailableFile notFound() {
            return new AvailableFile(null, HttpStatus.NOT_FOUND);
        }

        static AvailableFile gone() {
            return new AvailableFile(null, HttpStatus.GONE);
        }

        boolean found() {
            return file != null;
        }
    }
}
