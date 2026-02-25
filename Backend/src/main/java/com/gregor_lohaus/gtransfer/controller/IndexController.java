package com.gregor_lohaus.gtransfer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Value("${spring.servlet.multipart.max-file-size:10GB}")
    private String maxFileSize;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("maxFileSize", maxFileSize);
        return "index";
    }
}
