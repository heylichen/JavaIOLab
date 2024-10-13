package com.iolab.channel.socketchannel;

import com.iolab.channel.util.SleepUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChannelClient {

  public static void main(String[] args) {
    try {
      SocketChannel sc = SocketChannel.open();
      sc.configureBlocking(false);
      InetSocketAddress addr = new InetSocketAddress("localhost", 9999);
      sc.connect(addr);
      while (!sc.finishConnect())
        System.out.println("waiting to finish connection");

      ByteBuffer buffer = ByteBuffer.allocate(200);
      System.out.print("read from server:");
      int readBytes = 0;
      //当对方调用SocketChannel.close时，此处的SocketChannel.read返回-1, 退出while
      while ((readBytes = sc.read(buffer)) >= 0) {
        if (readBytes == 0) {
          SleepUtil.sleep(100);
          continue;
        }
        buffer.flip();
        while (buffer.hasRemaining())
          System.out.print((char) buffer.get());
        buffer.clear();
      }
      sc.close();
    } catch (IOException ioe) {
      System.err.println("I/O error: " + ioe.getMessage());
    }
  }
}