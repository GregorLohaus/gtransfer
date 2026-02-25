package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class StorageService implements TomlSerializable {
  @Property(name = "type")
  public StorageServiceType type;
  @Property(name = "root")
  public String path;
}
