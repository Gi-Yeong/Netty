#### ServerBootstrap 과는 다르게 클라이언트에서 사용하는 다일 소켓 채널에 대한 설정이 이므로 부모 자식이라는 관계에 해당하는 API 는 없다.

- group : 이벤트 루프 설정

- channel : 소켓 입출력 모드 설정
    - LocalChannel.class : 한 가상머신 안에서 가상 통신을 하고자 클라리언트 소켓 채널을 생성하는 클래스
    - OioChannel.class : 블로킹 모드의 클라이언트 소켓 채널을 생성하는 클래스
    - NioChannel.class : 논블로킹 모드의 클라이언트 소켓 채널을 생성하는 클래스
    - EpollSocketChannel.class : 리눅스 커널의 epoll 입출력 모드를 지원하는 클라이언트 소켓 채널을 생성하는 클래스
    - OioSctpChannel.class : SCTP 전송 계층을 사용하는 블로킹 모드의 클라이언트 소켓 채널을 생성하는 클래스
    - NioSctpChannel.class : SCTP 전송 계층을 사용하는 논블로킹 모드의 클라이언트 소켓 채널을 생성하는 클래스
    
- channelFactory : 소켓 입출력 모드 설정
    - 소켓의 입출력 모드를 설정하는 API 인 channelFactory 메서드는 channel 메서드와 동일하게 소켓의 입출력 모드를 설정하는 <br>
    API 다. ChannelFactory 인터페이스를 상속받은 클래스를 설정할 수 있으며 channel 메서드와 동일한 기능을 수행한다. <br>
    네티가 제공하는 ChannelFactory 인터페이스의 구현체로는 NioUdtProvider 가 있다.
    
- handler : 클라이언트 소켓 채널의 이벤트 핸들러 설정
    - ChannelInitializer 클래스를 통해서 등록 되며 클라이언트는 서버 소켓 채널이 존재하지 않으므로 handler 메서드를 통해서 등록 된다.
```java
    Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new EchoClientHandler());
                 }
             });
```

- option : 소켓 채널의 소켓 옵션 설정
    - 클라이언트 소켓 채널의 소켓 옵션을 설정한다.