package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class StorageService implements TomlSerializable {
  @Property(name = "type")
  public StorageServiceType type;
  @Property(name = "root")
  public String path;
  @Property(name = "bucket")
  public String bucket;
  @Property(name = "region")
  public String region;
  @Property(name = "endpoint")
  public String endpoint;
  @Property(name = "accessKeyId")
  public String accessKeyId;
  @Property(name = "secretAccessKey")
  public String secretAccessKey;
  @Property(name = "pathStyleAccessEnabled")
  public Boolean pathStyleAccessEnabled;
}
