package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Nested;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class SpringConfig implements TomlSerializable {
  @Nested(name = "datasource")
  public DataSourceConfig dataSourceConfig;
  @Nested(name = "jpa")
  public JpaConfig jpaConfig;
  @Nested(name = "servlet")
  public ServletConfig servletConfig;
}
