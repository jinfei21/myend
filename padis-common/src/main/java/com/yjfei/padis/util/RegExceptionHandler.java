package com.yjfei.padis.util;

import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(RegExceptionHandler.class);
    
    /**
     * 处理掉中断和连接失效异常并继续抛出RuntimeException.
     * 
     * @param cause 待处理的异常.
     */
    public static void handleException(final Exception cause) {
        if (isIgnoredException(cause) || isIgnoredException(cause.getCause())) {
            log.debug("padis: ignored exception for: {}", cause.getMessage());
        } else if (cause instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        } else {
            throw new RuntimeException(cause);
        }
    }
    
    private static boolean isIgnoredException(final Throwable cause) {
        return null != cause && (cause instanceof ConnectionLossException || cause instanceof NoNodeException || cause instanceof NodeExistsException);
    }
}
