package com.pingan.jinke.infra.padis.exceptions;

public class DataException extends PadisException {
  private static final long serialVersionUID = 3878126572474819403L;

  public DataException(String message) {
    super(message);
  }

  public DataException(Throwable cause) {
    super(cause);
  }

  public DataException(String message, Throwable cause) {
    super(message, cause);
  }
}
