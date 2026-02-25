package com.gregor_lohaus.gtransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.RestController;

import com.gregor_lohaus.gtransfer.config.ConfigRuntimeHints;
import com.gregor_lohaus.gtransfer.model.ModelRuntimeHints;
import com.gregor_lohaus.gtransfer.native_image.HibernateRuntimeHints;
import com.gregor_lohaus.gtransfer.native_image.WebRuntimeHints;

@SpringBootApplication
@RestController
@ImportRuntimeHints({ConfigRuntimeHints.class, HibernateRuntimeHints.class, ModelRuntimeHints.class, WebRuntimeHints.class})
public class GtransferApplication {
	public static void main(String[] args) {
		SpringApplication.run(GtransferApplication.class, args);
	}

}
