package com.netty.blocking_nonBlocking_Socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NonBlockingServer {
    private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
    private ByteBuffer buffer = ByteBuffer.allocateDirect(2 * 1024);


    private void startEchoServer() {
        try (
             Selector selector = Selector.open();
             // 2 자신에게 등록된 채널에 변경 사항이 발생 했는지 검사하고 변경 사항이 발생한 채널에 대한 접근을 가능하게 해준다.
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
             // 3 블로킹 소켓의 ServerSocket 에 대응되는 논블로킹 소켓의 서버 소켓채널을 생성한다.
             // 블로킹 소켓과 다르게 소켓 채널을 먼저 생성하고 사용할 포트를 바인딩 한다.
        ) {

            if ((serverSocketChannel.isOpen()) && (selector.isOpen())) { // 4 생성한 객체들이 정상적으로 생성 되었는지 확인한다.
                serverSocketChannel.configureBlocking(false); // 5 소켓 채널의 블로킹 모드는 true 이다. 별도로 지정하지 않으면 블로킹 모드로 동작한다.
                serverSocketChannel.bind(new InetSocketAddress(8888));
                // 6 클라이언트의 연결을 대기할 포트를 지정하고 생성된 ServerSocketChannel 객체에 할당한다.
                // 이 작업이 완료되면 ServerSocketChannel 객체가 지정된 포트로부터 클라이언트의 연결을 생성할 수 있다.

                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                // 7 ServerSocketChannel 객체를 Selector 객체에 등록한다.
                // Selector 가 감지할 이벤트는 연결 요청에 해당하는 SelectionKey.OP_WRITE 이다.
                System.out.println("접속 대기중");

                while (true) {
                    selector.select();
                    // 8 Selector 에 등록된 채널에서 변경 사항이 발생했는지 검사한다.
                    // Selector 에 아무런 I/O 이벤트도 발생 하지 않으면 스레드는 이 부분에서 블로킹 된다.
                    // I/O 이벤트가 발생하지 않을 떄 블로킹을 피하고 싶다면 selectNow 메서드를 사용하면 된다.

                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    // 9 Selector 에 등록된 채널 중에서 I/O 이벤트가 발생한 채널들의 목록을 조회한다.

                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove(); // 10 I/O 이벤트가 발생한 채널에서 동일한 이벤트가 감지되는 것을 방지하기 위하여 조회된 목록에서 제거 한다.

                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isAcceptable()) {           // 11 조회된 I/O 이벤트의 종류가 연결 요청인지 확인한다.
                            this.acceptOP(key, selector);   // 만약 연결 요청이벤트라면 연결처리 메서드로 이동한다.
                        } else if (key.isReadable()) { // 12 조회된 I/O 이벤트의 종류가 데이터 수신인지 확인한다.
                            this.readOP(key);          // 데이터 읽기 처리 메서드로 이동한다.
                        } else if (key.isWritable()) { // 13 조회된 I/O 이벤트의 종류가 데이터 쓰기 기능인지 확인한다.
                            this.writeOP(key);         // 데이터 읽기 처리 메서드로 이동한다.
                        } else {
                            System.out.println("서버 소켓을 생성하지 못했습니다.");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void acceptOP(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // 14 연결 요청 이벤트가 발생한 채널은 항상 ServerSocketChannel 이므로 이벤트가 발생한 채널을 ServerSocketChannel 로 캐스팅 한다.

        SocketChannel socketChannel = serverChannel.accept();
        // 15 ServerSocketChannel 을 사용하여 클라이언트의 연결을 수락하고 연결된 소켓 채널을 가져온다.

        socketChannel.configureBlocking(false); // 16 연결된 클라이언트 소켓 채널을 논 블로킹 모드로 설정한다.

        System.out.println("클라이언트 연결됨 : " + socketChannel.getRemoteAddress());

        keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
        socketChannel.register(selector, SelectionKey.OP_READ); // 17 클라이언트 소켓 채널을 Selector 에 등록하여 I/O 이벤트를 감시한다.
    }

    private void readOP(SelectionKey key) throws IOException {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear();

            int numRead = -1;
            try {
                numRead = socketChannel.read(buffer);
            } catch (IOException e) {
                System.err.println("데이터 읽기 에러!");
            }

            if (numRead == -1) {
                this.keepDataTrack.remove(socketChannel);
                System.out.println("클라이언트 연결 종료" + socketChannel.getRemoteAddress());
                socketChannel.close();
                key.cancel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println(new String(data, StandardCharsets.UTF_8) + " from " + socketChannel.getRemoteAddress());

            doEchoJop(key, data);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void writeOP(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        Iterator<byte[]> its = channelData.iterator();

        while (its.hasNext()) {
            byte[] it = its.next();
            its.remove();
            socketChannel.write(ByteBuffer.wrap(it));
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void doEchoJop(SelectionKey key, byte[] data) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.add(data);

        key.interestOps(SelectionKey.OP_WRITE);
    }

    public static void main(String[] args) {
        NonBlockingServer main = new NonBlockingServer();
        main.startEchoServer();
    }

}
