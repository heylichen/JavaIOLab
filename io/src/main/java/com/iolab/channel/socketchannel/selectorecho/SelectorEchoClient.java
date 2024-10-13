package com.iolab.channel.socketchannel.selectorecho;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class SelectorEchoClient {

  private static final int SELECT_TIMEOUT_MS = 500;
  private static final int BUF_SIZE = 1024;

  public static void main(String[] args) throws IOException {
    try (SocketChannel sc = SocketChannel.open()) {
      sc.configureBlocking(false);

      Selector selector = Selector.open();
      InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
      if (sc.connect(address)) {
        sc.register(selector, SelectionKey.OP_WRITE);
      } else {
        sc.register(selector, SelectionKey.OP_CONNECT);
      }

      boolean stop = false;
      while (!stop) {
        if (selector.select(SELECT_TIMEOUT_MS) <= 0) {
          System.out.print(".");
          continue;
        }

        Set<SelectionKey> selected = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selected.iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove();

          if (key.isConnectable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()) {
              if (channel.finishConnect()) {
                key.interestOps(SelectionKey.OP_WRITE);
              } else {
                throw new IOException("Failed to complete connection");
              }
            } else {
              key.interestOps(SelectionKey.OP_WRITE);
            }
          } else if (key.isValid() && key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
            int read = channel.read(buffer);
            //一次可能没有读完全部消息，但此处暂不考虑消息分界问题。
            if (read == -1) {
              System.out.println("Server closed the connection.");
              key.cancel();
            } else if (read > 0) {
              buffer.flip(); // 准备读取数据
              byte[] data = new byte[read];
              buffer.get(data);
              String receivedMessage = new String(data, StandardCharsets.UTF_8);
              System.out.println("Received echo: " + receivedMessage);
            }

            channel.close();
            stop = true;
          } else if (key.isValid() && key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.wrap("Hello, Server!".getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);
            key.interestOps(SelectionKey.OP_READ);
          }
        }
      }
    }
  }
}
