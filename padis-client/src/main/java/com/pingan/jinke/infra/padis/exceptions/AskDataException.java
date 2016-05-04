package com.pingan.jinke.infra.padis.exceptions;

import com.yjfei.cache.padis.common.HostAndPort;

public class AskDataException extends RedirectionException {
  private static final long serialVersionUID = 3878126572474819403L;

  public AskDataException(Throwable cause, HostAndPort targetHost, int slot) {
    super(cause, targetHost, slot);
  }

  public AskDataException(String message, Throwable cause, HostAndPort targetHost, int slot) {
    super(message, cause, targetHost, slot);
  }

  public AskDataException(String message, HostAndPort targetHost, int slot) {
    super(message, targetHost, slot);
  }

}
