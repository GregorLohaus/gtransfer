package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class UploadConfig implements TomlSerializable {
  @Property(name = "maxDownloadLimit")
  public Integer maxDownloadLimit;
  @Property(name = "maxExpiryDays")
  public Integer maxExpiryDays;
  @Property(name = "cleanupEnabled")
  public Boolean cleanupEnabled;
}
