package com.iolab.channel.socketchannel.errorecho;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for handling non-blocking mode operations on SocketChannels and ServerSocketChannels.
 * This class is generated with the assistance of Qwen AI to demonstrate the usage of Java Channels in non-blocking mode.
 */
public class SocketUtils {

  /**
   * Reads all available data from a SocketChannel into a ByteBuffer.
   *
   * @param channel The SocketChannel to read from.
   * @param buffer  The ByteBuffer to store the read data.
   * @return The byte array containing the read data.
   * @throws IOException If an I/O error occurs.
   */
  public static byte[] readFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
    int bytesRead;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // only -1 indicate end of stream
    // 2个问题:
    // 1 如果read返回-1，当作EOF。server永远读不到-1, 死锁。
    // 2 如果read返回0，当作EOF。client可能读不到Server 返回的数据，就结束了。
    while ((bytesRead = channel.read(buffer)) > 0) {
      //possibly 0
      if (bytesRead == 0) {
        continue;
      }
      buffer.flip();
      baos.write(buffer.array(), buffer.position(), buffer.limit() - buffer.position());
      buffer.clear();
    }
    return baos.toByteArray();
  }

  /**
   * Writes all data from a ByteBuffer to a SocketChannel.
   *
   * @param channel The SocketChannel to write to.
   * @param buffer  The ByteBuffer containing the data to write.
   * @throws IOException If an I/O error occurs.
   */
  public static void writeFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
    while (buffer.hasRemaining()) {
      channel.write(buffer);
    }
  }

  /**
   * Connects to a server using a non-blocking SocketChannel with a timeout.
   *
   * @param channel The SocketChannel to connect.
   * @param host    The server host name or IP address.
   * @param port    The server port.
   * @param timeout The connection timeout in milliseconds.
   * @throws IOException          If an I/O error occurs.
   * @throws InterruptedException If the thread is interrupted.
   */
  public static void connectToServer(SocketChannel channel, String host, int port, long timeout) throws IOException, InterruptedException {
    channel.connect(new InetSocketAddress(host, port));
    long startTime = System.currentTimeMillis();
    while (!channel.finishConnect()) {
      if (System.currentTimeMillis() - startTime >= timeout) {
        throw new IOException("Connection timed out");
      }
      TimeUnit.MILLISECONDS.sleep(100); // Wait for connection completion
    }
  }

  /**
   * Accepts a client connection using a non-blocking ServerSocketChannel with a timeout.
   *
   * @param serverChannel The ServerSocketChannel to accept connections.
   * @param timeout       The accept timeout in milliseconds.
   * @return The accepted client SocketChannel.
   * @throws IOException          If an I/O error occurs.
   * @throws InterruptedException If the thread is interrupted.
   */
  public static SocketChannel acceptConnection(ServerSocketChannel serverChannel, long timeout) throws IOException, InterruptedException {
    long startTime = System.currentTimeMillis();
    SocketChannel client = null;
    do {
      client = serverChannel.accept();
      if (client == null) {
        TimeUnit.MILLISECONDS.sleep(100); // Sleep if no client connects
      }
    } while (client == null && System.currentTimeMillis() - startTime < timeout);
    if (client == null) {
      throw new IOException("Accept timed out");
    }
    return client;
  }
}