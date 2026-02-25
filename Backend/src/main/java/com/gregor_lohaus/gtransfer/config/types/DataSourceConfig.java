package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class DataSourceConfig implements TomlSerializable {
  @Property(name = "url")
  public String url;
  @Property(name = "username")
  public String username;
  @Property(name = "password")
  public String password;
}
