package com.gregor_lohaus.gtransfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.RestController;

import com.gregor_lohaus.gtransfer.config.ConfigRuntimeHints;
import com.gregor_lohaus.gtransfer.model.ModelRuntimeHints;
import com.gregor_lohaus.gtransfer.native_image.HibernateRuntimeHints;
import com.gregor_lohaus.gtransfer.native_image.WebRuntimeHints;
import com.gregor_lohaus.gtransfer.services.filecleanup.FileCleanupService;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RestController
@EnableScheduling
@ImportRuntimeHints({ConfigRuntimeHints.class, HibernateRuntimeHints.class, ModelRuntimeHints.class, WebRuntimeHints.class})
public class GtransferApplication {
	@Autowired
	private FileCleanupService cleanupService;
	public static void main(String[] args) {
		SpringApplication.run(GtransferApplication.class, args);
	}

}
