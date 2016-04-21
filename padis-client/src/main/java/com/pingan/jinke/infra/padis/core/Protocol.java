package com.pingan.jinke.infra.padis.core;

import static com.pingan.jinke.infra.padis.common.Constant.ASK_RESPONSE;
import static com.pingan.jinke.infra.padis.common.Constant.ASTERISK_BYTE;
import static com.pingan.jinke.infra.padis.common.Constant.CLUSTERDOWN_RESPONSE;
import static com.pingan.jinke.infra.padis.common.Constant.COLON_BYTE;
import static com.pingan.jinke.infra.padis.common.Constant.DOLLAR_BYTE;
import static com.pingan.jinke.infra.padis.common.Constant.MINUS_BYTE;
import static com.pingan.jinke.infra.padis.common.Constant.MOVED_RESPONSE;
import static com.pingan.jinke.infra.padis.common.Constant.PLUS_BYTE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pingan.jinke.infra.padis.common.HostAndPort;
import com.pingan.jinke.infra.padis.common.ProtocolCommand;
import com.pingan.jinke.infra.padis.exceptions.AskDataException;
import com.pingan.jinke.infra.padis.exceptions.ClusterException;
import com.pingan.jinke.infra.padis.exceptions.ConnectionException;
import com.pingan.jinke.infra.padis.exceptions.DataException;
import com.pingan.jinke.infra.padis.exceptions.MovedDataException;
import com.pingan.jinke.infra.padis.util.PadisInputStream;
import com.pingan.jinke.infra.padis.util.PadisOutputStream;
import com.pingan.jinke.infra.padis.util.SafeEncoder;

public class Protocol {
	public static final byte[] BYTES_TRUE = toByteArray(1);
	public static final byte[] BYTES_FALSE = toByteArray(0);

	private Protocol() {
		// this prevent the class from instantiation
	}

	public static void sendCommand(final PadisOutputStream os, final ProtocolCommand command, final byte[]... args) {
		sendCommand(os, command.getRaw(), args);
	}

	private static void sendCommand(final PadisOutputStream os, final byte[] command, final byte[]... args) {
		try {
			os.write(ASTERISK_BYTE);
			os.writeIntCrLf(args.length + 1);
			os.write(DOLLAR_BYTE);
			os.writeIntCrLf(command.length);
			os.write(command);
			os.writeCrLf();

			for (final byte[] arg : args) {
				os.write(DOLLAR_BYTE);
				os.writeIntCrLf(arg.length);
				os.write(arg);
				os.writeCrLf();
			}
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}

	public static String readErrorLineIfPossible(PadisInputStream is) {
		final byte b = is.readByte();
		// if buffer contains other type of response, just ignore.
		if (b != MINUS_BYTE) {
			return null;
		}
		return is.readLine();
	}

	public static Object read(final PadisInputStream is) {
		return process(is);
	}

	private static Object process(final PadisInputStream is) {

		final byte b = is.readByte();
		if (b == PLUS_BYTE) {
			return processStatusCodeReply(is);
		} else if (b == DOLLAR_BYTE) {
			return processBulkReply(is);
		} else if (b == ASTERISK_BYTE) {
			return processMultiBulkReply(is);
		} else if (b == COLON_BYTE) {
			return processInteger(is);
		} else if (b == MINUS_BYTE) {
			processError(is);
			return null;
		} else {
			throw new ConnectionException("Unknown reply: " + (char) b);
		}
	}

	private static void processError(final PadisInputStream is) {
		String message = is.readLine();
		// TODO: I'm not sure if this is the best way to do this.
		// Maybe Read only first 5 bytes instead?
		if (message.startsWith(MOVED_RESPONSE)) {
			String[] movedInfo = parseTargetHostAndSlot(message);
			throw new MovedDataException(message, new HostAndPort(movedInfo[1], Integer.valueOf(movedInfo[2])),
					Integer.valueOf(movedInfo[0]));
		} else if (message.startsWith(ASK_RESPONSE)) {
			String[] askInfo = parseTargetHostAndSlot(message);
			throw new AskDataException(message, new HostAndPort(askInfo[1], Integer.valueOf(askInfo[2])),
					Integer.valueOf(askInfo[0]));
		} else if (message.startsWith(CLUSTERDOWN_RESPONSE)) {
			throw new ClusterException(message);
		}
		throw new DataException(message);
	}

	private static String[] parseTargetHostAndSlot(String clusterRedirectResponse) {
		String[] response = new String[3];
		String[] messageInfo = clusterRedirectResponse.split(" ");
		String[] targetHostAndPort = messageInfo[2].split(":");
		response[0] = messageInfo[1];
		response[1] = targetHostAndPort[0];
		response[2] = targetHostAndPort[1];
		return response;
	}

	private static Long processInteger(final PadisInputStream is) {
		return is.readLongCrLf();
	}

	private static byte[] processStatusCodeReply(final PadisInputStream is) {
		return is.readLineBytes();
	}

	private static byte[] processBulkReply(final PadisInputStream is) {
		final int len = is.readIntCrLf();
		if (len == -1) {
			return null;
		}

		final byte[] read = new byte[len];
		int offset = 0;
		while (offset < len) {
			final int size = is.read(read, offset, (len - offset));
			if (size == -1)
				throw new ConnectionException("It seems like server has closed the connection.");
			offset += size;
		}

		// read 2 more bytes for the command delimiter
		is.readByte();
		is.readByte();

		return read;
	}

	private static List<Object> processMultiBulkReply(final PadisInputStream is) {
		final int num = is.readIntCrLf();
		if (num == -1) {
			return null;
		}
		final List<Object> ret = new ArrayList<Object>(num);
		for (int i = 0; i < num; i++) {
			try {
				ret.add(process(is));
			} catch (DataException e) {
				ret.add(e);
			}
		}
		return ret;
	}

	public static final byte[] toByteArray(final boolean value) {
		return value ? BYTES_TRUE : BYTES_FALSE;
	}

	public static final byte[] toByteArray(final int value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public static final byte[] toByteArray(final long value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public static final byte[] toByteArray(final double value) {
		return SafeEncoder.encode(String.valueOf(value));
	}
}
