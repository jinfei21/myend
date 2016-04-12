package com.pingan.jinke.infra.padis.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pingan.jinke.infra.padis.exceptions.ConnectionException;

public class PadisInputStream extends FilterInputStream {

  protected final byte[] buf;

  protected int count, limit;

  public PadisInputStream(InputStream in, int size) {
    super(in);
    if (size <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    buf = new byte[size];
  }

  public PadisInputStream(InputStream in) {
    this(in, 8192);
  }

  public byte readByte() throws ConnectionException {
    ensureFill();
    return buf[count++];
  }

  public String readLine() {
    final StringBuilder sb = new StringBuilder();
    while (true) {
      ensureFill();

      byte b = buf[count++];
      if (b == '\r') {
        ensureFill(); // Must be one more byte

        byte c = buf[count++];
        if (c == '\n') {
          break;
        }
        sb.append((char) b);
        sb.append((char) c);
      } else {
        sb.append((char) b);
      }
    }

    final String reply = sb.toString();
    if (reply.length() == 0) {
      throw new ConnectionException("It seems like server has closed the connection.");
    }

    return reply;
  }

  public byte[] readLineBytes() {

    /*
     * This operation should only require one fill. In that typical case we optimize allocation and
     * copy of the byte array. In the edge case where more than one fill is required then we take a
     * slower path and expand a byte array output stream as is necessary.
     */

    ensureFill();

    int pos = count;
    final byte[] buf = this.buf;
    while (true) {
      if (pos == limit) {
        return readLineBytesSlowly();
      }

      if (buf[pos++] == '\r') {
        if (pos == limit) {
          return readLineBytesSlowly();
        }

        if (buf[pos++] == '\n') {
          break;
        }
      }
    }

    final int N = (pos - count) - 2;
    final byte[] line = new byte[N];
    System.arraycopy(buf, count, line, 0, N);
    count = pos;
    return line;
  }

  /**
   * Slow path in case a line of bytes cannot be read in one #fill() operation. This is still faster
   * than creating the StrinbBuilder, String, then encoding as byte[] in Protocol, then decoding
   * back into a String.
   */
  private byte[] readLineBytesSlowly() {
    ByteArrayOutputStream bout = null;
    while (true) {
      ensureFill();

      byte b = buf[count++];
      if (b == '\r') {
        ensureFill(); // Must be one more byte

        byte c = buf[count++];
        if (c == '\n') {
          break;
        }

        if (bout == null) {
          bout = new ByteArrayOutputStream(16);
        }

        bout.write(b);
        bout.write(c);
      } else {
        if (bout == null) {
          bout = new ByteArrayOutputStream(16);
        }

        bout.write(b);
      }
    }

    return bout == null ? new byte[0] : bout.toByteArray();
  }

  public int readIntCrLf() {
    return (int) readLongCrLf();
  }

  public long readLongCrLf() {
    final byte[] buf = this.buf;

    ensureFill();

    final boolean isNeg = buf[count] == '-';
    if (isNeg) {
      ++count;
    }

    long value = 0;
    while (true) {
      ensureFill();

      final int b = buf[count++];
      if (b == '\r') {
        ensureFill();

        if (buf[count++] != '\n') {
          throw new ConnectionException("Unexpected character!");
        }

        break;
      } else {
        value = value * 10 + b - '0';
      }
    }

    return (isNeg ? -value : value);
  }

  public int read(byte[] b, int off, int len) throws ConnectionException {
    ensureFill();

    final int length = Math.min(limit - count, len);
    System.arraycopy(buf, count, b, off, length);
    count += length;
    return length;
  }

  /**
   * This methods assumes there are required bytes to be read. If we cannot read anymore bytes an
   * exception is thrown to quickly ascertain that the stream was smaller than expected.
   */
  private void ensureFill() throws ConnectionException {
    if (count >= limit) {
      try {
        limit = in.read(buf);
        count = 0;
        if (limit == -1) {
          throw new ConnectionException("Unexpected end of stream.");
        }
      } catch (IOException e) {
        throw new ConnectionException(e);
      }
    }
  }
}
