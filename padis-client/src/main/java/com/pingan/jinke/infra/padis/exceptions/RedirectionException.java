package com.pingan.jinke.infra.padis.exceptions;

import com.pingan.jinke.infra.padis.common.HostAndPort;

public class RedirectionException extends DataException {
  private static final long serialVersionUID = 3878126572474819403L;

  private HostAndPort targetNode;
  private int slot;

  public RedirectionException(String message, HostAndPort targetNode, int slot) {
    super(message);
    this.targetNode = targetNode;
    this.slot = slot;
  }

  public RedirectionException(Throwable cause, HostAndPort targetNode, int slot) {
    super(cause);
    this.targetNode = targetNode;
    this.slot = slot;
  }

  public RedirectionException(String message, Throwable cause, HostAndPort targetNode, int slot) {
    super(message, cause);
    this.targetNode = targetNode;
    this.slot = slot;
  }

  public HostAndPort getTargetNode() {
    return targetNode;
  }

  public int getSlot() {
    return slot;
  }
}
