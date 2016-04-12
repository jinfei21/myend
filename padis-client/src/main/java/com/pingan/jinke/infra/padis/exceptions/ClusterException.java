package com.pingan.jinke.infra.padis.exceptions;

public class ClusterException extends DataException {
  private static final long serialVersionUID = 3878126572474819403L;

  public ClusterException(Throwable cause) {
    super(cause);
  }

  public ClusterException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClusterException(String message) {
    super(message);
  }
}
