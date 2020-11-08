package com.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new EchoClientHandler());
                        }
                    });

            // 비동기 입출력 메서드인 connect 를 호출한다.
            // connect 메서드는 메서드의 호출 결과로 ChannelFuture 객체를 돌려주는데 이 객체를 통해서 비동기 메서드의 처리 결과를 확인 할 수 있다.
            // ChannelFuture 객체의 sync 메서드는 ChannelFuture 객체의 요청이 완료 될 때까지 대기 한다.
            // 단, 요청이 실패하면 예외를 던진다. 즉, connect 메서드의 처리가 완료 될 때까지 다음 라인으로 진행 하지 않는다.
            ChannelFuture f = b.connect("localhost", 8888).sync();

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
