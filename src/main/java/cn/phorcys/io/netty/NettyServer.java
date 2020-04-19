package cn.phorcys.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {
    private int port = 8000;

    public NettyServer(int port) {
        this.port = port;
    }

    public void serverStart() {

        NioEventLoopGroup mastser = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(mastser, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Handler());
            }
        });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            mastser.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    class Handler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("read action");
            ByteBuf buf = (ByteBuf) msg;
            System.out.println(buf.toString());
            ctx.writeAndFlush("success received");
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }

    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer(8000);
        server.serverStart();
    }
}
