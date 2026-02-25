package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class SslConfig implements TomlSerializable {
  @Property(name = "enabled")
  public Boolean enabled;
  @Property(name = "certificate")
  public String certificate;
  @Property(name = "certificate-private-key")
  public String certificatePrivateKey;
}
