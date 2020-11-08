package com.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 데이터 수신 이벤트 처리 메서드
     * 클라이언트로부터 데이터의 수신이 이루어졌을 때 네티가 자동으로 호출하는 이벤트 메서드
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
        // 수신된 데이터를 가지고 있는 네티의 바이터 버퍼 객체로부터 문자열 데이터를 읽어 온다
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
        
        System.out.println("수신한 문자열 [" + readMessage + ']');

        // ctx 는 ChannelHandlerContext 인터페이스의 객체로서 채널 파이프라인에 대한 이벤트를 처리한다
        // 여기서는 서버에 연결된 클라이언트 채널로 입력받은 데이터를 그대로 전송한다
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // channelRead 이벤트의 처리가 완료된 후 자동으로 수행되는 이벤트 메서드로서
        // 채널 파이프라인에서 저장된 버퍼를 전송하는 flush 메서드를 호출한다
        ctx.flush();
    }
}
