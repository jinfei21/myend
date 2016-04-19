package com.pingan.jinke.infra.padis.storage;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.pingan.jinke.infra.padis.common.CoordinatorRegistryCenter;
import com.pingan.jinke.infra.padis.common.TransactionExecutionCallback;
import com.pingan.jinke.infra.padis.util.RegExceptionHandler;

public class NodeStorage {

    private final CoordinatorRegistryCenter coordinatorRegistryCenter;
       
    
    public NodeStorage(final CoordinatorRegistryCenter coordinatorRegistryCenter){
    	this.coordinatorRegistryCenter = coordinatorRegistryCenter;
    	
    }
    
    public void addCacheData(final String path) {
    	this.coordinatorRegistryCenter.addCacheData(path);
    }
    
    /**
     * 判断节点是否存在.
     * 
     * @param path 节点路径
     * @return 节点是否存在
     */
    public boolean isNodePathExisted(final String path) {
        return coordinatorRegistryCenter.isExisted(path);
    }
    
    
    /**
     * 获取节点数据.
     * 
     * @param path 节点路径
     * @return 节点数据值
     */
    public String getNodePathData(final String path) {
        return coordinatorRegistryCenter.get(path);
    }
    
    
    /**
     * 直接从注册中心而非本地缓存获取节点数据.
     * 
     * @param path 节点路径
     * @return 节点数据值
     */
    public String getNodePathDataDirectly(final String path) {
        return coordinatorRegistryCenter.getDirectly(path);
    }
    
    
    /**
     * 获取节点子节点名称列表.
     * 
     * @param path 节点路径
     * @return 节点子节点名称列表
     */
    public List<String> getNodePathChildrenKeys(final String path) {
        return coordinatorRegistryCenter.getChildrenKeys(path);
    }
    
    
    /**
     * 如果存在则创建节点.
     * 
     * @param path 节点路径
     */
    public void createNodePathIfNeeded(final String path) {
        if (!isNodePathExisted(path)) {
            coordinatorRegistryCenter.persist(path, "");
        }
    }
    
    /**
     * 删除节点.
     * 
     * @param path 节点路径
     */
    public void removeNodeIfExisted(final String path) {
        if (isNodePathExisted(path)) {
            coordinatorRegistryCenter.remove(path);
        }
    }
    
    
    /**
     * 填充临时节点数据.
     * 
     * @param path 节点路径
     * @param value 节点数据值
     */
    public void fillEphemeralNodePath(final String path, final Object value) {
        coordinatorRegistryCenter.persistEphemeral(path, value.toString());
    }
    
    /**
     * 填充临时节点数据.
     * 
     * @param path 节点路径
     */
    public String fillEphemeralSeqNodePath(String path, final Object value) {
    	return coordinatorRegistryCenter.persistEphemeralSequential(path,value.toString());
    }
    /**
     * 更新节点数据.
     * 
     * @param path 节点名称
     * @param value 节点数据值
     */
    public void updateNodePath(final String path, final Object value) {
        coordinatorRegistryCenter.update(path, value.toString());
    }
    
    
    /**
     * 替换作业节点数据.
     * 
     * @param path 节点路径
     * @param value 待替换的数据
     */
    public void replaceNodePath(final String path, final Object value) {
        coordinatorRegistryCenter.persist(path, value.toString());
    }

    /**
     * 在事务中执行操作.
     * 
     * @param callback 执行操作的回调
     */
    public void executeInTransaction(final TransactionExecutionCallback callback) {
        try {
            CuratorTransactionFinal curatorTransactionFinal = getClient().inTransaction().check().forPath("/").and();
            callback.execute(curatorTransactionFinal);
            curatorTransactionFinal.commit();
        //CHECKSTYLE:OFF
        } catch (final Exception ex) {
        //CHECKSTYLE:ON
            RegExceptionHandler.handleException(ex);
        }
    }
    
    /**
     * 注册连接状态监听器.
     */
    public void addConnectionStateListener(final ConnectionStateListener listener) {
        getClient().getConnectionStateListenable().addListener(listener);
    }
    
    private CuratorFramework getClient() {
        return (CuratorFramework) coordinatorRegistryCenter.getRawClient();
    }
    
    /**
     * 注册数据监听器.
     */
    public void addDataListener(final TreeCacheListener listener,String path) {
        TreeCache cache = (TreeCache) coordinatorRegistryCenter.getRawCache(path);
        cache.getListenable().addListener(listener);
    }
    
    /**
     * 获取注册中心当前时间.
     * 
     * @return 注册中心当前时间
     */
    public long getRegistryCenterTime() {
        return coordinatorRegistryCenter.getRegistryCenterTime("/padis/systemTime/current");
    }
}
