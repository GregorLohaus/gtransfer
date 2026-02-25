package com.gregor_lohaus.gtransfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gregor_lohaus.gtransfer.services.filewriter.AbstractStorageService;

@RestController
public class Env {
	@Autowired
	private ConfigurableEnvironment env;
	@Autowired
	private AbstractStorageService storageService;
  @GetMapping("/env")
  public String env() {
		StringBuilder b = new StringBuilder();
		MutablePropertySources sources = this.env.getPropertySources();
		sources.forEach((var m) -> {
			b.append(m.toString());
			b.append("\n");
		});
		b.append(storageService.getClass().toString());
		return b.toString();
  }
}
