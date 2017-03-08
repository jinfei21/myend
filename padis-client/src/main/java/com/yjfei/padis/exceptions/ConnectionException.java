package com.yjfei.padis.exceptions;

public class ConnectionException extends PadisException {
  private static final long serialVersionUID = 3878126572474819403L;

  public ConnectionException(String message) {
    super(message);
  }

  public ConnectionException(Throwable cause) {
    super(cause);
  }

  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
