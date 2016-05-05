package com.yjfei.cache.padis;

import static com.yjfei.cache.padis.common.Status.ONLINE;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.yjfei.cache.padis.PadisConfig;
import com.yjfei.cache.padis.common.CoordinatorRegistryCenter;
import com.yjfei.cache.padis.common.ZookeeperConfiguration;
import com.yjfei.cache.padis.node.Custom;
import com.yjfei.cache.padis.node.CustomNode;
import com.yjfei.cache.padis.node.Group;
import com.yjfei.cache.padis.node.GroupNode;
import com.yjfei.cache.padis.storage.AbstractNodeListener;
import com.yjfei.cache.padis.storage.NodeStorage;
import com.yjfei.cache.padis.storage.ZookeeperRegistryCenter;
import com.yjfei.cache.padis.util.IPUtils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;

@Slf4j
public class ClusterInfoCache {

	private NodeStorage nodeStorage;

	private PadisConfig config;

	private AtomicReference<Custom> atomicCustom;

	public ClusterInfoCache(PadisConfig config) {
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
				new ZookeeperConfiguration(config.getZkAddr(), "padis", 1000, 3000, 3));
		regCenter.init();
		this.nodeStorage = new NodeStorage(regCenter);
		this.config = new PadisConfig(config);
		registerCustom();
	}

	private void registerCustom() {
		CustomNode customNode = new CustomNode(config.getInstance());
		String ip = IPUtils.getIP();
		Custom custom = new Custom();
		custom.setCreate(System.currentTimeMillis());
		custom.setModify(System.currentTimeMillis());
		custom.setHost(ip);
		custom.setStatus(ONLINE);
		custom.setLimit(-1);
		String data = JSON.toJSONString(custom);
		String customPath = nodeStorage.fillEphemeralSeqNodePath(customNode.getCustomPath(ip), data);
		this.nodeStorage.addDataListener(new CustomStatusListener(), customPath);
		
		this.atomicCustom = new AtomicReference<Custom>(custom);
	}

	class CustomStatusListener extends AbstractNodeListener {

		@Override
		protected void dataChanged(CuratorFramework client, TreeCacheEvent event, String path) {
			if (Type.NODE_UPDATED == event.getType()) {

				try {
					String json = new String(event.getData().getData());
					Custom custom = JSON.parseObject(json, Custom.class);
					log.info("custom:{}", json);
					atomicCustom.set(custom);
				} catch (Throwable t) {
					log.error("update custom fail!", t);
				}
			}

		}

	}

	public Set<HostAndPort> getServers() {
		Set<HostAndPort> set = Sets.newHashSet();
		GroupNode groupNode = new GroupNode();
		List<String> nodes = this.nodeStorage.getNodePathChildrenKeys(groupNode.getRootGroupPath());
		for (String node : nodes) {
			try {
				String data = this.nodeStorage.getNodePathDataDirectly(groupNode.getGroupPath(node));
				Group group = JSON.parseObject(data, Group.class);
				HostAndPort host = new HostAndPort(group.getMaster().getHost(), group.getMaster().getPort());
				set.add(host);
			} catch (Throwable t) {
				log.error("get remote cluster fail.", t);
			}
		}

		return set;
	}
	
	public Custom getCustom(){
		return this.atomicCustom.get();
	}

	public GenericObjectPoolConfig getPoolConfig() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(getMaxIdle());
		poolConfig.setMaxTotal(getMaxTotal());
		poolConfig.setMinIdle(getMinIdle());
		return poolConfig;
	}

	public void setNameSpace(String nameSpace) {
		this.config.setNameSpace(nameSpace);
	}

	public String getNameSpace() {
		return this.config.getNameSpace();
	}

	public int getMaxIdle() {
		return this.config.getMaxIdle();
	}

	public String getInstance() {
		return this.config.getInstance();
	}

	public int getMaxRedirections() {
		return this.config.getMaxRedirections();
	}

	public int getConnectionTimeout() {
		return this.config.getConnectionTimeout();
	}

	public int getSoTimeout() {
		return this.config.getSoTimeout();
	}

	public int getMaxTotal() {
		return this.config.getMaxTotal();
	}

	public int getMinIdle() {
		return this.config.getMinIdle();
	}
	
	public void close(){
		this.nodeStorage.close();
	}
}
