package com.gregor_lohaus.gtransfer.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import com.gregor_lohaus.gtransfer.config.types.*;

import io.github.wasabithumb.jtoml.serial.reflect.adapter.TypeAdapter;

public class ConfigRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection().registerType(Config.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(StorageService.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(StorageServiceType.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(SpringConfig.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(JpaConfig.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(DataSourceConfig.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(ServletConfig.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(MultipartConfig.class,
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
    hints.reflection().registerType(TypeAdapter.class,
        MemberCategory.ACCESS_DECLARED_FIELDS,
        MemberCategory.ACCESS_PUBLIC_FIELDS);
  }
}
