package com.iolab.channel.socketchannel;

import com.iolab.channel.util.SleepUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ChannelServer {

  public static void main(String[] args) throws IOException {
    System.out.println("Starting server...");
    ServerSocketChannel ssc = ServerSocketChannel.open();
    ssc.bind(new InetSocketAddress(9999));
    ssc.configureBlocking(false);

    String msg = "Server address: " + ssc.socket().getLocalSocketAddress();
    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
    while (true) {
      System.out.print(".");
      SocketChannel sc = ssc.accept();

      //因为是non blocking, sc可能为null
      if (sc != null) {
        System.out.println("\nReceived connection from " +
            sc.socket().getRemoteSocketAddress());
        buffer.rewind();
        sc.write(buffer);
        sc.close();
      } else
        SleepUtil.sleep(500);
    }
  }
}