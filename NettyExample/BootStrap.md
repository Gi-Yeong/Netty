- group : 이벤트 루프 설정
    - 데이터 송수신 처리를 위한 이벤트 루프를 설정하는 메서드
    
- channel : 소켓 입출력 모드 설정
    - LocalServerChannel.class : 하나의 자바 가상 머신에서 가상 통신을 위한 서버 소켓 채널을 생성하는 클래스
    통상적으로 하나의 애플리케이션 내에서 클라이언트와 서버를 모두 구현하고 애플리케이션 안에서 소켓 통신을 수행할 때 사용한다.
    - OioServerChannel.class : 블로킹 모드의 서버 소켓 채널을 생성하는 클래스
    - NioServerChannel.class : 논블로킹 모드의 서버 소켓 채널을 생성하는 클래스
    - EpollServerSocketChannel.class : 리눅스 커널의 epoll 입출력 모드를 지원하는 서버 소켓 채널을 생성하는 클래스
    - OioSctpServerChannel.class : SCTP 전송 계층을 사용하는 블로킹 모드의 서버 소켓 채널을 생성하는 클래스
    - NioSctpServerChannel.class : SCTP 전송 계층을 사용하는 논블로킹 모드의 서버 소켓 채널을 생성하는 클래스
    - NioUdtByteAcceptorChannel.class : UDP 프로토콜을 지원하는 논블로킹 모드의 서버 소켓 채널을 생성하는 클래스.<br>
    내부적으로 스트림 데이터를 처리하도록 구현되어 있으며 barchart-udt 라이브러리를 사용한다.
    - NioUdtMessageAcceptorChannel.class : UDP 프로토콜을 지원하는 블로킹 모드의 서버 소켓 채널을 생성하는 클래스.<br>
    내부적으로 데이터그램 패킷을 처리하도록 구현되어 있다.

- channelFactory : 소켓 입출력 모드 설정
    - 소켓의 입출력 모드를 설정하는 API 인 channelFactory 메서드는 channel 메서드와 동일하게 소켓의 입출력 모드를 설정하는 <br>
    API 다. ChannelFactory 인터페이스를 상속받은 클래스를 설정할 수 있으며 channel 메서드와 동일한 기능을 수행한다. <br>
    네티가 제공하는 ChannelFactory 인터페이스의 구현체로는 NioUdtProvider 가 있다.
    
- handler : 서버 소켓 채널의 이벤트 핸들러 설정
    - 서버 소켓 채널에서 발생하는 이벤트를 수신하여 처리 한다. handler 메서드로 등록된 이벤트 핸들로는 서버 소켓 채널에서 발생한 이벤트만 처리한다.
    
- childHandler : 소켓 채널의 데이터 가공 핸들러 설정
    - 클라이언트 소켓 채널로 송수신되는 데이터를 가공하는 데이터 핸들러 설정 API. ChannelHandler 인터페이스를 구현한 클래스를 인수로 입력할 수 있다.<br>
    이 메서드를 통해서 등록되는 이벤트 핸들러는 서버에 연결된 클라리언트 소켓 채널에서 발생하는 이벤트를 수신하여 처리.
    
- option : 서버 소켓 채널의 소켓 옵션 설정
    - 소켓 옵션은 애플리케이션의 값을 바꾸는 것이 아니라 커널에서 사용되는 값을 변경한다.<br>

|옵션|설명|기본값|
|------|---|---|
|TCP_NODELAY|데이터 송수신에 Nagle 알고리즘의 비활성화 여부를 지정한다|false(비활성화)|
|SO_KEEPALIVE|운영체제에서 지정된 시간에 한번씩 keepalive 패킷을 상대방에게 전송한다|false(비활성화)|
|SO_SNDBUF|상대방으로 송신할 커널 송신 버퍼의 크기|커널 설정에 따라 다름|
|SO_RCVBUF|상대방으로 부터 수신할 커널 수신 버퍼의 크기|커널 설정에 따라 다름|
|SO_REUSEADDR|TIME_WAIT 상태의 포트를 서버 소켓에 바인드 할 수 있게 한다|false(비활성화)|
|SO_LINGER|소켓을 닫을 때 커널의 송신 버퍼에 전송되지 않은 데이터의 전송 대기시간을 지정한다|false(비활성화)|
|SO_BACKLOG|동시에 수용 가능한 소켓 연결 요청 수||

> Nagle : `가능하면 데이터를 나누어 보내지 말고 한꺼번에 보내라` 라는 원칙을 기반으로 만들어진 알고리즘
> TCP/IP 에서 데이터를 전송하려면 데이터에 헤더를 포함해야 하는데, 약 50 바이트 정도 한다.
> 즉, 데이터를 여러번 나누어 보내면 각 패킷에 불필요한 50 바이트의 헤더 정보로 인한 오버헤드가 발생하기 때문에
> 이를 방지하고자 데이터를 모아서 전송하라는 의도
> 전송 측은 ACK 를 수신하기 전까지 다음 데이터를 보내지 않는다.
> 그러므로 빠른 응답시간이 필요한 네트워크 애플리케이션에서는 좋지 않은 결과를 가져온다.
> **그럴 때는 TCP_NODELAY 옵션을 활성화 하여 네이글 알고리즘을 사용하지 않도록 설정한다.**

> SO_REUSEADDR : 해당 포트가 TIME_WAIT 더라도 bind 할 수 있게 하는 옵션

> SO_BACKLOG : 지정한 값이 서버 소켓이 수용할 수 있는 동시 연결 수가 아니다.
> 해당 옵션은 SYN_RECEIVED 상태로 변경된 소켓 연결을 가지고 있는 큐의 크기를 설정하는 옵션
> 즉, 큐의 크기는 서버가 받아 들일 수 있는 동시 연결 요청수가 된다.
> 단, 이 값을 너무 크게 설정하면 클라이언트의 연결 요청이 폭주할 때 연결 대기 시간이 길어져 클라이언트에서 연결 타임아웃이 발생 할 수 있으니
> 너무 크게 잡으면 안된다.
> 반대로 너무 작게 잡으면 클라이언트가 연결을 생성하지 못하는 상황이 발생한다.
```java
ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 1) // 서버가 동시에 하나의 연결 요청만 수용하도록 1로 설정 되었다.
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new EchoServerHandler());
                 }
             });
```  

- childOption : option 메서드와 같이 소켓 채널에 소켓 옵션을 설정한다.<br>
option 메서드는 서버 소켓 채널의 옵션을 설정하는데 반해 childOption 메서드는 서버에 접속한 클라이언트 소켓 채널에 대한 옵션을 설정하는데 사용한다.

> SO_LINGER : SO_REUSEADDR 옵션과 같이 소켓 종료와 관련이 있다.
> 소켓에 대하여 close 메서드를 호출한 이후의 제어권은 어플리케이션에서 운영체제로 넘어간다.
> 이때 커널 버퍼에 아직 전송 되지 않은 데이터가 남아 있으면 어떻게 처리할지 지정하는 옵션이다.
> 이 옵션은 `옵션의 사용 여부`와 `타임아웃 값`을 설정할 수 있고, 기본값은 `사용하지 않음`이다.
> 이 옵션을 켜면 close 메서드가 호출 되었을 때 커널 버퍼의 데이터를 상대방으로 모두 전송하고 상대방의 ACK 패킷을 기다린다.
> 포트 상태가 TIME_WAIT 로 전환되는 것을 방지 하기 위해 SO_LINGER 옵션을 활성화 하고 타임아웃값을 0 으로 하는 편법도 있다.
```java
ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childOption(ChannelOption.SO_LINGER, 0) // SO_LINGER 옵션을 사용 하고 있다.
             .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new EchoServerHandler());
                }
            });
```
> SO_LINGER 옵션에 0을 주었기 때문에, 커널 버퍼에 남은 데이터를 상대방 소켓 채널로 모두 전송하고 즉시 연결을 끊는다.
> TIME_WAIT 이 발생하지 않는 장점이 있지만, 마지막으로 전송한 데이터가 모두 전송되었는지 확인 할 수 있는 방법이 없다.
> 또한 블로킹 모드로 사용 할 경우 타임아웃 값을 1초로 지정하면, 클라이언트로부트 ACK 패킷이 도착하지 않으면 지정된 타임아웃 시간동안 블로킹 된다.
> **자바 API 문서에는 `논 블로킹 모드의 소켓에서 SO_LINGER 옵션의 동작이 정의되어 있지 않다` 라고 명시되어 있지만 리눅스 커널에서는 블로킹 되도록 구현되어 있다**