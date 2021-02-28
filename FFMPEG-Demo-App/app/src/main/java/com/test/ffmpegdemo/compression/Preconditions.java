package com.test.ffmpegdemo.compression;

final class Preconditions {

  static void checkState(final Object errorMessage, final boolean expression) {
    if (!expression) {
      throw new IllegalStateException(String.valueOf(errorMessage));
    }
  }
}