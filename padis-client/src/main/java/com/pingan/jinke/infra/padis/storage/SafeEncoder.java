package com.pingan.jinke.infra.padis.storage;

import static com.pingan.jinke.infra.padis.common.Constant.*;

import java.io.UnsupportedEncodingException;

public final class SafeEncoder {
  private SafeEncoder(){
    throw new InstantiationError( "Must not instantiate this class" );
  }

  public static byte[][] encodeMany(final String... strs) {
    byte[][] many = new byte[strs.length][];
    for (int i = 0; i < strs.length; i++) {
      many[i] = encode(strs[i]);
    }
    return many;
  }

  public static byte[] encode(final String str) {
    try {
      if (str == null) {
        throw new IllegalArgumentException("value sent to redis cannot be null");
      }
      return str.getBytes(CHARSET);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encode(final byte[] data) {
    try {
      return new String(data, CHARSET);
    } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
    }
  }
}