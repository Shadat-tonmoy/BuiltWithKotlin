package com.test.ffmpegdemo.compression;

public final class EncodingException extends Exception {
  EncodingException(String message) {
    super(message);
  }

  EncodingException(String message, Exception inner) {
    super(message, inner);
  }
}
