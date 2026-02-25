package com.gregor_lohaus.gtransfer.config;

import io.github.wasabithumb.jtoml.serial.reflect.ReflectTomlSerializer;
import io.github.wasabithumb.jtoml.serial.reflect.adapter.TypeAdapter;
import io.github.wasabithumb.jtoml.serial.reflect.adapter.TypeAdapters;
import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.table.TomlTable;
import com.gregor_lohaus.gtransfer.config.types.Config;
import com.gregor_lohaus.gtransfer.config.types.StorageServiceType;

public class ConfigSerializer {
  static final private ReflectTomlSerializer<Config> s;
  static {
    TypeAdapter<StorageServiceType> fileWritTypeAdapter = TypeAdapter.of(
      StorageServiceType.class,
      (TomlValue v) -> StorageServiceType.fromToml(v),
      (StorageServiceType f) -> StorageServiceType.toToml(f)
    );
    TypeAdapters typeAdapters = TypeAdapters.builder()
      .add(TypeAdapters.standard())
      .add(fileWritTypeAdapter)
      .build();
    s = new ReflectTomlSerializer<Config>(Config.class,typeAdapters); 
  }

  public static Config fromToml(TomlTable t) {
    return s.fromToml(t);
  }

  public static TomlTable toToml(Config c) {
    return s.toToml(c);
  }
}
