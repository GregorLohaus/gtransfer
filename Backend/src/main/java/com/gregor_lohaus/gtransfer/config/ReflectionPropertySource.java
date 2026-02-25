package com.gregor_lohaus.gtransfer.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.core.env.PropertySource;

import com.gregor_lohaus.gtransfer.config.annotations.Named;
import com.gregor_lohaus.gtransfer.config.annotations.Nested;
import com.gregor_lohaus.gtransfer.config.annotations.NoPrefix;
import com.gregor_lohaus.gtransfer.config.annotations.Property;

public class ReflectionPropertySource<T> extends PropertySource<T> {
  private HashMap<String, Object> fields;
  private ArrayList<String> propNameBuffer;

  public ReflectionPropertySource(String name, T source) {
    super(name);
    this.fields = new HashMap<String, Object>();
    this.propNameBuffer = new ArrayList<String>();
    this.propNameBuffer.add(this.getName());
    this.getFields(source, 0);
  }

  private boolean hasPrefix() {
    if (propNameBuffer.size() > 0 && propNameBuffer.getFirst() == this.getName()) {
      return true;
    }
    return false;
  }

  private void handleNoPrefix(Field field) {
    NoPrefix np = field.getAnnotation(NoPrefix.class);
    if (np != null && this.hasPrefix()) {
      this.propNameBuffer.remove(0);
    }
  }

  public void handleAnnotated(Field field, Object object, int depth) throws IllegalAccessException {
    Annotation annotation;
    annotation = field.getAnnotation(Property.class);
    if (annotation == null) {
      annotation = field.getAnnotation(Nested.class);
    }
    Optional<String> name = Named.nameOf(annotation);
    if (name.isEmpty()) {
      return;
    }
    field.setAccessible(true);
    Object fieldValue = field.get(object);
    if (fieldValue == null) {
      return;
    }
    handleNoPrefix(field);
    propNameBuffer.add(name.get());
    switch (annotation) {
      case Nested _ -> getFields(fieldValue, depth + 1);
      case Property _ -> fields.put(String.join(".", propNameBuffer), fieldValue);
      case null -> {}
      default -> {}
    }
    propNameBuffer.removeLast();
  }

  private void getFields(Object object, int depth) {
    Class<?> c = object.getClass();
    Field[] fields = c.getDeclaredFields();
    for (Field field : fields) {
      try {
        handleAnnotated(field, object, depth);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        System.exit(1);
      }
      if (depth == 0 && !hasPrefix()) {
        propNameBuffer.addFirst(this.getName());
      }
    }
  };

  @Override
  public @Nullable Object getProperty(String name) {
    return this.fields.get(name);
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    this.fields.forEach((String f, Object o) -> {
      out.append(f + ":" + o.toString() + "\n");
    });
    return out.toString();
  }

}
