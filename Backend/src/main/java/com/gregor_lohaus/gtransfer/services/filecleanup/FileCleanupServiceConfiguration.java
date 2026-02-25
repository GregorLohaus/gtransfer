
package com.gregor_lohaus.gtransfer.services.filecleanup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gregor_lohaus.gtransfer.config.types.StorageServiceType;

@Configuration
public class FileCleanupServiceConfiguration {
  @Bean
  public FileCleanupService fileCleanupService(
    @Value("${gtransfer-config.upload.cleanupEnabled}")
    Boolean enabled
  ) {
    return new FileCleanupService(enabled);
  }
}
