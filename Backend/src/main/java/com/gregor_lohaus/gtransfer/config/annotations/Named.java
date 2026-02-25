package com.gregor_lohaus.gtransfer.config.annotations;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface Named {
  static Optional<String> nameOf(Annotation annotation) {
    return switch (annotation) {
      case Property p -> Optional.of(p.name());
      case Nested n -> Optional.of(n.name());
      case null -> Optional.empty();
      default -> Optional.empty();
    };
  }
}
