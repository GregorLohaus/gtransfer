package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class MultipartConfig implements TomlSerializable {
  @Property(name = "max-file-size")
  public String maxFileSize;
  @Property(name = "max-request-size")
  public String maxRequestSize;
}
