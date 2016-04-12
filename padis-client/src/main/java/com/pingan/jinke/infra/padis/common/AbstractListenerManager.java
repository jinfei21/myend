package com.pingan.jinke.infra.padis.common;

import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.pingan.jinke.infra.padis.core.ClusterManager;
import com.pingan.jinke.infra.padis.storage.NodeStorage;

/**
 * 注册中心的监听器管理者的抽象类.
 * 
 * @author feiyongjun
 */
public abstract class AbstractListenerManager {
    
    protected final NodeStorage nodeStorage;
    protected final String instance;
    protected final ClusterManager clusterManager;
    
    protected AbstractListenerManager(final String instance,final CoordinatorRegistryCenter coordinatorRegistryCenter,final ClusterManager clusterManager) {
    	this.nodeStorage = new NodeStorage(coordinatorRegistryCenter);
    	this.instance = instance;
    	this.clusterManager = clusterManager;
    	
    }

    /**
     * 开启监听器.
     */
    public abstract void start();
    
    protected void addDataListener(final TreeCacheListener listener,String path) {
    	nodeStorage.addDataListener(listener,path);
    }
    
    protected void addConnectionStateListener(final ConnectionStateListener listener) {
    	nodeStorage.addConnectionStateListener(listener);
    }
}