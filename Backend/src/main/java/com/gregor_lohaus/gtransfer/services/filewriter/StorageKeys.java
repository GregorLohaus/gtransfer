package com.gregor_lohaus.gtransfer.services.filewriter;

public final class StorageKeys {
  private StorageKeys() {}

  public static String chunk(String id, int index) {
    return id + "/chunks/" + index;
  }
}
