package com.gregor_lohaus.gtransfer.services.filewriter;

import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

public class DummyStorageService extends AbstractStorageService {

  public DummyStorageService(Path root) {
    super(root);
  }

  @Override
  public OptionalLong put(String id, byte[] data) {
    return OptionalLong.empty();
  }

  @Override
  public Optional<byte[]> get(String id) {
    return Optional.empty();
  }
}
