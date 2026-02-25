package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Nested;
import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class ServerConfig implements TomlSerializable {
  @Property(name = "port")
  public Integer port;
  @Nested(name = "ssl")
  public SslConfig sslConfig;
}
