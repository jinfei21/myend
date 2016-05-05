package com.yjfei.cache.padis.core;

import com.yjfei.cache.padis.common.HostAndPort;
import com.yjfei.cache.padis.common.ObjectFactory;

public class ClientFactory implements ObjectFactory<Client> {

	private HostAndPort hostPort;

	public ClientFactory(String host, int port) {
		this(new HostAndPort(host, port));
	}

	public ClientFactory(HostAndPort hostPort) {
		this.hostPort = hostPort;
	}

	@Override
	public Client make() {
		return new Client(hostPort);
	}

}
