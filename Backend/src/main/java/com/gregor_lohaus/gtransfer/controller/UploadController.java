package com.gregor_lohaus.gtransfer.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gregor_lohaus.gtransfer.model.File;
import com.gregor_lohaus.gtransfer.model.FileRepository;
import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;

@RestController
public class UploadController {

    @Autowired
    private AbstractStorageService storageService;

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hash") String hash,
            @RequestParam("name") String name) throws IOException {

        storageService.put(hash, file.getBytes());
        fileRepository.save(new File(hash, hash, name, null));

        return ResponseEntity.ok(Map.of("id", hash));
    }
}
