package com.iolab.channel.socketchannel.selectorecho;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorEchoServer {
  private static final int SELECT_TIMEOUT_MS = 500;
  private static final int BUF_SIZE = 1024;

  public static void main(String[] args) throws IOException {
    ServerSocketChannel ssc = ServerSocketChannel.open();
    ssc.configureBlocking(false);
    ssc.bind(new InetSocketAddress("127.0.0.1", 8888));

    Selector selector = Selector.open();
    ssc.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      // 1 select for ready events
      if (selector.select(SELECT_TIMEOUT_MS) <= 0) {
        System.out.print(".");
        continue;
      }

      //2 get Iterator for ready events (SelectionKey)
      Set<SelectionKey> selected = selector.selectedKeys();
      Iterator<SelectionKey> iterator = selected.iterator();
      //3 for each ready event  (SelectionKey)
      while (iterator.hasNext()) {
        // 3.1 get and remove key
        SelectionKey key = iterator.next();
        iterator.remove();

        // 3.2 check key type and process.
        if (key.isAcceptable()) {
          SocketChannel socketChannel = ssc.accept();
          socketChannel.configureBlocking(false);
          socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(BUF_SIZE));
        } else if (key.isReadable()) {
          SocketChannel channel = (SocketChannel) key.channel();
          ByteBuffer buffer = (ByteBuffer) key.attachment();
          long read = channel.read(buffer);
          //一次可能没有读完全部消息，但此处暂不考虑消息分界问题。
          if (read > 0) {
            key.interestOps(SelectionKey.OP_WRITE);
            System.out.println(read + " bytes read from client");
          } else if (read == -1) {
            channel.close();
          }
        } else if (key.isValid() && key.isWritable()) {
          SocketChannel channel = (SocketChannel) key.channel();
          ByteBuffer buffer = (ByteBuffer) key.attachment();
          buffer.flip();
          channel.write(buffer);
          if (!buffer.hasRemaining()) {
            channel.close();
          } else {
            key.interestOps(SelectionKey.OP_WRITE);
          }
        }
      }
    }
  }
}
