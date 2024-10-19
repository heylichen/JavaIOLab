package demo.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoClientHandler extends ChannelInboundHandlerAdapter { // (1)

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("client msg sent!");
    ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
    ByteBuf m = (ByteBuf) msg; // (1)
    try {
      System.out.println("Client received: " + m.toString(CharsetUtil.UTF_8));
      ctx.close();
    } finally {
      m.release();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
    // Close the connection when an exception is raised.
    cause.printStackTrace();
    ctx.close();
  }
}
