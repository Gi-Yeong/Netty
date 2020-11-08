package com.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * channelActive 이벤트는 ChannelInboundHandler 에 정의된 이벤트로써 소켓 채널이 최초 활성화 되었을 때 실행 된다.
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String sendMessage = "Hello, Netty";

        ByteBuf messageByteBuffer = Unpooled.buffer();
        messageByteBuffer.writeBytes(sendMessage.getBytes());

        StringBuilder builder = new StringBuilder();
        builder.append("전송한 문자열 [");
        builder.append(sendMessage);
        builder.append("]");

        System.out.println(builder.toString());
        
        // writeAndFlush 는 내부적으로 데이터 기록과 전송의 두 가지 메서드를 호출한다.
        // 첫 번째는 채널에 데이터를 기록하는 write 메서드이며 두 번째는 채널에 기록된 데이터를 서버로 전송하는 flush 메서드다. 
        ctx.writeAndFlush(messageByteBuffer); 
    }

    /**
     * 서버로 부터 수신된 데이터가 있을 때 호출되는 네티 이벤트 메서드
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

        StringBuilder builder = new StringBuilder();
        builder.append("수신한 문자열 [");
        builder.append(readMessage);
        builder.append("]");

        System.out.println(builder.toString());
    }

    /**
     * 수신된 데이터를 모두 읽었을 때 호출되는 이벤트 메서드. channelRead 메서드의 수행이 완료되고 나서 자동으로 호출
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 수신된 데이터를 모두 읽은 후 서버와 연결된 채널을 닫는다. 이후 데이터 송수신 채널은 닫히게 되고 클라이언트 프로그램은 종료
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
