package com.gregor_lohaus.gtransfer.config.types;

import io.github.wasabithumb.jtoml.serial.TomlSerializable;
import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.primitive.TomlPrimitive;;
public enum StorageServiceType implements TomlSerializable {
  LOCAL,
  S3,
  DUMMY;
  public static StorageServiceType fromToml(TomlValue value) {
    return switch (value.asPrimitive().asString().toLowerCase()) {
      case "dummy" -> DUMMY;
      case "local" -> LOCAL;
      case "s3" -> S3;
      default -> throw new IllegalArgumentException("couldnt parse filewriter type");
    };
  }
  public static TomlValue toToml(StorageServiceType t) {
    return switch (t) {
      case LOCAL -> TomlPrimitive.of("local");
      case S3 -> TomlPrimitive.of("s3");
      case DUMMY -> TomlPrimitive.of("dummy");
      default -> throw new IllegalArgumentException();
    };
  }
}
