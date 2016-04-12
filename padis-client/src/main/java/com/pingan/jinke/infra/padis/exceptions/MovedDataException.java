package com.pingan.jinke.infra.padis.exceptions;

import com.pingan.jinke.infra.padis.common.HostAndPort;

public class MovedDataException extends RedirectionException {
  private static final long serialVersionUID = 3878126572474819403L;

  public MovedDataException(String message, HostAndPort targetNode, int slot) {
    super(message, targetNode, slot);
  }

  public MovedDataException(Throwable cause, HostAndPort targetNode, int slot) {
    super(cause, targetNode, slot);
  }

  public MovedDataException(String message, Throwable cause, HostAndPort targetNode, int slot) {
    super(message, cause, targetNode, slot);
  }
}
