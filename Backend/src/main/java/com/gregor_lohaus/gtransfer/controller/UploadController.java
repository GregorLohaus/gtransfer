package com.gregor_lohaus.gtransfer.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.OptionalLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.gregor_lohaus.gtransfer.model.File;
import com.gregor_lohaus.gtransfer.model.FileRepository;
import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;
import com.gregor_lohaus.gtransfer.services.filewriter.StorageKeys;

@Controller
public class UploadController {

    @Value("${gtransfer-config.upload.maxDownloadLimit:100}")
    private Integer maxDownloadLimit;

    @Value("${gtransfer-config.upload.maxExpiryDays:30}")
    private Integer maxExpiryDays;

    @Autowired
    private AbstractStorageService storageService;

    @Autowired
    private FileRepository fileRepository;

    @GetMapping("/upload/options")
    public String options(@RequestParam String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("maxExpiryDays", maxExpiryDays);
        model.addAttribute("maxDownloadLimit", maxDownloadLimit);
        return "upload/options :: form";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("hash") String hash,
            @RequestParam("name") String name,
            @RequestParam("chunkCount") Integer chunkCount,
            @RequestParam(required = false) Integer expiryDays,
            @RequestParam(required = false) Integer downloadLimit) {
        if (!isValidId(hash) || chunkCount == null || chunkCount < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload metadata");
        }

        int days = expiryDays != null ? Math.min(expiryDays, maxExpiryDays) : maxExpiryDays;
        Integer limit = downloadLimit != null ? Math.min(downloadLimit, maxDownloadLimit) : null;

        File f = new File(hash, hash, name, LocalDateTime.now().plusDays(days));
        f.setChunkCount(chunkCount);
        f.setDownloadLimit(limit);
        fileRepository.save(f);

        return "upload/result :: view";
    }

    @PostMapping("/upload/chunk")
    public ResponseEntity<Void> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("hash") String hash,
            @RequestParam("index") Integer index) throws IOException {
        if (!isValidId(hash) || index == null || index < 0) {
            return ResponseEntity.badRequest().build();
        }

        OptionalLong written = storageService.put(StorageKeys.chunk(hash, index), chunk.getBytes());
        if (written.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.noContent().build();
    }

    private boolean isValidId(String id) {
        return id != null && id.matches("[a-f0-9]{64}");
    }
}
