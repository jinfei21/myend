package com.pingan.jinke.infra.padis.common;

import static com.yjfei.cache.padis.common.Constant.DEFAULT_TIMEOUT;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import com.pingan.jinke.infra.padis.core.Protocol;
import com.pingan.jinke.infra.padis.exceptions.ConnectionException;
import com.pingan.jinke.infra.padis.util.PadisInputStream;
import com.pingan.jinke.infra.padis.util.PadisOutputStream;
import com.yjfei.cache.padis.common.HostAndPort;
import com.yjfei.cache.padis.util.IOUtils;
import com.yjfei.cache.padis.util.SafeEncoder;

public class Connection {

	private HostAndPort hostport;
	private Socket socket;
	private PadisOutputStream outputStream;
	private PadisInputStream inputStream;
	private int connectionTimeout = DEFAULT_TIMEOUT;
	private int soTimeout = DEFAULT_TIMEOUT;
	private boolean broken = false;

	public Connection() {
		this.hostport = new HostAndPort();
	}

	public Connection(HostAndPort hostport) {
		this.hostport = hostport;
	}

	public Socket getSocket() {
		return socket;
	}
	
	public HostAndPort getHostPort(){
		return this.hostport;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public void setTimeoutInfinite() {
		try {
			if (!isConnected()) {
				connect();
			}
			socket.setSoTimeout(0);
		} catch (SocketException ex) {
			broken = true;
			throw new ConnectionException(ex);
		}
	}

	public void rollbackTimeout() {
		try {
			socket.setSoTimeout(soTimeout);
		} catch (SocketException ex) {
			broken = true;
			throw new ConnectionException(ex);
		}
	}

	public boolean isConnected() {
		return socket != null && socket.isBound() && !socket.isClosed() && socket.isConnected()
				&& !socket.isInputShutdown() && !socket.isOutputShutdown();
	}

	public void connect() {
		if (!isConnected()) {
			try {
				socket = new Socket();
				socket.setReuseAddress(true);
				socket.setKeepAlive(true); // Will monitor the TCP connection is
				// valid
				socket.setTcpNoDelay(true); // Socket buffer Whetherclosed, to
				// ensure timely delivery of data
				socket.setSoLinger(true, 0); // Control calls close () method,
				// the underlying socket is closed
				// immediately

				socket.connect(new InetSocketAddress(hostport.getHost(), hostport.getPort()), connectionTimeout);
				socket.setSoTimeout(soTimeout);

				outputStream = new PadisOutputStream(socket.getOutputStream());
				inputStream = new PadisInputStream(socket.getInputStream());
			} catch (IOException ex) {
				broken = true;
				throw new ConnectionException(ex);
			}
		}
	}

	public void close() {
		disconnect();
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				outputStream.flush();
				socket.close();
			} catch (IOException ex) {
				broken = true;
				throw new ConnectionException(ex);
			} finally {
				IOUtils.closeQuietly(socket);
			}
		}
	}

	protected Connection sendCommand(final ProtocolCommand cmd, final String... args) {
		final byte[][] bargs = new byte[args.length][];
		for (int i = 0; i < args.length; i++) {
			bargs[i] = SafeEncoder.encode(args[i]);
		}
		return sendCommand(cmd, bargs);
	}

	protected Connection sendCommand(final ProtocolCommand cmd, final byte[]... args) {
		try {
			connect();
			Protocol.sendCommand(outputStream, cmd, args);
			return this;
		} catch (ConnectionException ex) {
			/*
			 * When client send request which formed by invalid protocol, Redis
			 * send back error message before close connection. We try to read
			 * it to provide reason of failure.
			 */
			try {
				String errorMessage = Protocol.readErrorLineIfPossible(inputStream);
				if (errorMessage != null && errorMessage.length() > 0) {
					ex = new ConnectionException(errorMessage, ex.getCause());
				}
			} catch (Exception e) {
				/*
				 * Catch any IOException or JedisConnectionException occurred
				 * from InputStream#read and just ignore. This approach is safe
				 * because reading error message is optional and connection will
				 * eventually be closed.
				 */
			}
			// Any other exceptions related to connection?
			broken = true;
			throw ex;
		}
	}

	protected void flush() {
		try {
			outputStream.flush();
		} catch (IOException ex) {
			broken = true;
			throw new ConnectionException(ex);
		}
	}

	public String getStatusCodeReply() {
		flush();
		final byte[] resp = (byte[]) readProtocolWithCheckingBroken();
		if (null == resp) {
			return null;
		} else {
			return SafeEncoder.encode(resp);
		}
	}

	protected Object readProtocolWithCheckingBroken() {
		try {
			return Protocol.read(inputStream);
		} catch (ConnectionException exc) {
			broken = true;
			throw exc;
		}
	}

	public byte[] getBinaryBulkReply() {
		flush();
		return (byte[]) readProtocolWithCheckingBroken();
	}

	public String getBulkReply() {
		final byte[] result = getBinaryBulkReply();
		if (null != result) {
			return SafeEncoder.encode(result);
		} else {
			return null;
		}
	}

	public Long getIntegerReply() {
		flush();
		return (Long) readProtocolWithCheckingBroken();
	}
	
	 @SuppressWarnings("unchecked")
	public List<byte[]> getBinaryMultiBulkReply() {
	    flush();
	    return (List<byte[]>) readProtocolWithCheckingBroken();
	}
}
