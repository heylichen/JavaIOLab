package com.iolab.channel.socketchannel.errorecho;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Demonstrates a simple non-blocking echo client using NIO channels.
 * This class is generated with the assistance of Qwen AI to demonstrate the usage of Java Channels in non-blocking mode.
 */
public class EchoClientChannel {

  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 8080;
  private static final long CONNECT_TIMEOUT = 2000; // 1 second

  /**
   * Main method that sets up the client and sends a message to the server.
   *
   * @param args Command line arguments (not used).
   */
  public static void main(String[] args) {
    try (SocketChannel client = SocketChannel.open()) {
      client.configureBlocking(false);
      // Connect to the server
      System.out.println("connecting to server ... ");
      SocketUtils.connectToServer(client, SERVER_ADDRESS, SERVER_PORT, CONNECT_TIMEOUT);
      System.out.println("connected to server. ");

      // Send data to the server
      System.out.println("write to server ");
      ByteBuffer sendBuffer = ByteBuffer.wrap("Hello, Server!".getBytes());
      SocketUtils.writeFully(client, sendBuffer);

      // Receive the server's response
      ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
      System.out.println("reading from server ");
      byte[] data = SocketUtils.readFully(client, receiveBuffer);
      System.out.println("received from server:" + new String(data));
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}