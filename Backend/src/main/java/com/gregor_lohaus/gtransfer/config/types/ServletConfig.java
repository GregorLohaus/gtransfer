package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Nested;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class ServletConfig implements TomlSerializable {
  @Nested(name = "multipart")
  public MultipartConfig multipartConfig;
}
