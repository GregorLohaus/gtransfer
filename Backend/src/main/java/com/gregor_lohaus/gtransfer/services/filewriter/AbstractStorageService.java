package com.gregor_lohaus.gtransfer.services.filewriter;

import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

public abstract class AbstractStorageService {
  protected Path root;

  public AbstractStorageService(Path root) {
    this.root = root;
  }

  abstract public OptionalLong put(String id, byte[] data);
  abstract public Optional<byte[]> get(String id);
}
