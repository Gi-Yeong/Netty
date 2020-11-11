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