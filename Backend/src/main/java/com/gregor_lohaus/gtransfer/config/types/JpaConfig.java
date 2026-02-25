package com.gregor_lohaus.gtransfer.config.types;

import com.gregor_lohaus.gtransfer.config.annotations.Property;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;

public class JpaConfig implements TomlSerializable {
  @Property(name = "hibernate.ddl-auto")
  public String ddlAuto;
  @Property(name = "show-sql")
  public boolean showSql;
  @Property(name = "properties.hibernate.dialect")
  public String dialect;
}
