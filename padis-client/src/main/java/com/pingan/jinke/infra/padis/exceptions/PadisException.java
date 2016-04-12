package com.pingan.jinke.infra.padis.exceptions;

public class PadisException extends RuntimeException {
  private static final long serialVersionUID = -2946266495682282677L;

  public PadisException(String message) {
    super(message);
  }

  public PadisException(Throwable e) {
    super(e);
  }

  public PadisException(String message, Throwable cause) {
    super(message, cause);
  }
}
