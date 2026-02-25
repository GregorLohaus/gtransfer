package com.gregor_lohaus.gtransfer.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.gregor_lohaus.gtransfer.model.File;
import com.gregor_lohaus.gtransfer.model.FileRepository;
import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;

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
            @RequestParam("file") MultipartFile file,
            @RequestParam("hash") String hash,
            @RequestParam("name") String name,
            @RequestParam(required = false) Integer expiryDays,
            @RequestParam(required = false) Integer downloadLimit,
            Model model) throws IOException {

        storageService.put(hash, file.getBytes());

        int days = expiryDays != null ? Math.min(expiryDays, maxExpiryDays) : maxExpiryDays;
        Integer limit = downloadLimit != null ? Math.min(downloadLimit, maxDownloadLimit) : null;

        File f = new File(hash, hash, name, LocalDateTime.now().plusDays(days));
        f.setDownloadLimit(limit);
        fileRepository.save(f);

        model.addAttribute("id", hash);
        return "upload/result :: view";
    }
}
