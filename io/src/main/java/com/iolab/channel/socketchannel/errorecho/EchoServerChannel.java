package com.iolab.channel.socketchannel.errorecho;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates a simple non-blocking echo server using NIO channels.
 * This class is generated with the assistance of Qwen AI to demonstrate the usage of Java Channels in non-blocking mode.
 */
public class EchoServerChannel {

  private static final int PORT = 8080;
  private static final long ACCEPT_TIMEOUT = 1000; // 1 second

  /**
   * Main method that sets up the server and starts listening for client connections.
   *
   * @param args Command line arguments (not used).
   */
  public static void main(String[] args) {
    try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
      // Bind the server channel to the specified port
      serverChannel.bind(new InetSocketAddress(PORT));

      // Main loop, continuously listening for client connections
      while (true) {
        System.out.println("waiting for client... ");
        SocketChannel client = SocketUtils.acceptConnection(serverChannel, ACCEPT_TIMEOUT);
        System.out.println("\ngot client. ");
        handleClientConnection(client);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }
  }

  /**
   * Handles the incoming client connection by reading data and echoing it back.
   *
   * @param client The client SocketChannel.
   * @throws IOException If an I/O error occurs.
   */
  private static void handleClientConnection(SocketChannel client) throws IOException {
    try (client) {
      client.configureBlocking(false);
      // Read data from the client
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      System.out.println("reading client ... ");
      byte[] data = SocketUtils.readFully(client, buffer);
      if (data != null) {
        // Echo the data back to the client
        ByteBuffer echoBuffer = ByteBuffer.wrap(data);
        System.out.println("writing client ：" + new String(data, StandardCharsets.UTF_8));
        SocketUtils.writeFully(client, echoBuffer);
        Thread.sleep(500);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}