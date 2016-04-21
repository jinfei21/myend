package com.pingan.jinke.infra.padis.storage;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * 作业注册中心的监听器.
 * 
 * @author feiyongjun
 */
public abstract class AbstractNodeListener implements TreeCacheListener {
    
    @Override
    public final void childEvent(final CuratorFramework client, final TreeCacheEvent event) throws Exception {
        String path = null == event.getData() ? "" : event.getData().getPath();
        if (path.isEmpty()) {
            return;
        }
        dataChanged(client, event, path);
    }
    
    protected abstract void dataChanged(final CuratorFramework client, final TreeCacheEvent event, final String path);
}