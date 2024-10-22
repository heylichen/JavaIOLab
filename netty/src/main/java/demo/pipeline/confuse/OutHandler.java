package demo.pipeline.confuse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Handles a server-side channel.
 */
public class OutHandler extends ChannelOutboundHandlerAdapter { // (1)

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

    ctx.writeAndFlush(msg, promise);
    System.out.println(ctx.name() + " channel ");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
    // Close the connection when an exception is raised.
    cause.printStackTrace();
    ctx.close();
  }
}