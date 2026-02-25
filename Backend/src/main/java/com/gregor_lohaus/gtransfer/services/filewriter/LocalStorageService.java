package com.gregor_lohaus.gtransfer.services.filewriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

public class LocalStorageService extends AbstractStorageService {

  public LocalStorageService(Path root) {
    super(root);
  }

  @Override
  public OptionalLong put(String id, byte[] data) {
    try {
      Files.createDirectories(root);
      Files.write(root.resolve(id), data);
      return OptionalLong.of(data.length);
    } catch (IOException e) {
      return OptionalLong.empty();
    }
  }

  @Override
  public Optional<byte[]> get(String id) {
    try {
      Path target = root.resolve(id);
      if (!Files.exists(target)) return Optional.empty();
      return Optional.of(Files.readAllBytes(target));
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean delete(String id) {
    try {
      return Files.deleteIfExists(root.resolve(id));
    } catch (IOException e) {
      return false;
    }
  }
}
