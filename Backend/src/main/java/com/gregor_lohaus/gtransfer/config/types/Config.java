package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Nested;
import com.gregor_lohaus.gtransfer.config.annotations.NoPrefix;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class Config implements TomlSerializable {
  @Nested(name = "spring")
  @NoPrefix
  public SpringConfig springConfig;
  @Nested(name = "server")
  @NoPrefix
  public ServerConfig serverConfig;
  @Nested(name = "storageService")
  public StorageService storageService;
  @Nested(name = "upload")
  public UploadConfig uploadConfig;
}
