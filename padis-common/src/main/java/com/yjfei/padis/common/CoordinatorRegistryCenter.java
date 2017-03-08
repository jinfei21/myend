package com.yjfei.padis.common;

import java.util.List;

/**
 * 用于协调分布式服务的注册中心.
 * 
 * @author feiyongjun
 */
public interface CoordinatorRegistryCenter extends RegistryCenter {
    
    /**
     * 直接从注册中心而非本地缓存获取数据.
     * 
     * @param key 键
     * @return 值
     */
    String getDirectly(String key);
    
    /**
     * 获取子节点名称集合.
     * 
     * @param key 键
     * @return 子节点名称集合
     */
    List<String> getChildrenKeys(String key);
    
    /**
     * 持久化临时注册数据.
     * 
     * @param key 键
     * @param value 值
     */
    void persistEphemeral(String key, String value);
    
    /**
     * 持久化临时顺序注册数据.
     * 
     * @param key 键
     * @param value 值
     */
    String persistEphemeralSequential(String key, String value);
    
    /**
     * 添加本地缓存.
     * 
     * @param cachePath 需加入缓存的路径
     */
    void addCacheData(String cachePath);
    
    /**
     * 获取注册中心数据缓存对象.
     * 
     * @param cachePath 缓存的节点路径
     * @return 注册中心数据缓存对象
     */
    Object getRawCache(String cachePath);
}