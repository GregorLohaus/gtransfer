package com.gregor_lohaus.gtransfer.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gregor_lohaus.gtransfer.model.File;
import com.gregor_lohaus.gtransfer.model.FileRepository;
import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;

@Controller
public class DownloadController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AbstractStorageService storageService;

    @GetMapping("/download/{id}")
    public String page(@PathVariable String id, Model model) {
        fileRepository.findById(id)
                .ifPresent(f -> model.addAttribute("filename", f.getName()));
        return "download/page";
    }

    @GetMapping("/download/{id}/data")
    @ResponseBody
    @Transactional
    public ResponseEntity<byte[]> data(@PathVariable String id) {
        Optional<File> fileOpt = fileRepository.findById(id);
        if (fileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        File file = fileOpt.get();

        // Check expiry
        if (file.getExpireyDateTime() != null && LocalDateTime.now().isAfter(file.getExpireyDateTime())) {
            storageService.delete(id);
            fileRepository.delete(file);
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        // Check download limit before serving
        if (file.getDownloadLimit() != null && file.getDownloads() >= file.getDownloadLimit()) {
            storageService.delete(id);
            fileRepository.delete(file);
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        Optional<byte[]> data = storageService.get(id);
        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Increment counter
        file.setDownloads(file.getDownloads() + 1);
        fileRepository.save(file);

        // Clean up if limit now reached
        if (file.getDownloadLimit() != null && file.getDownloads() >= file.getDownloadLimit()) {
            storageService.delete(id);
            fileRepository.delete(file);
        }

        String disposition = "attachment; filename=\""
                + file.getName().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data.get());
    }
}
