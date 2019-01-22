package socket;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @program: MyUtils
 * @description: SocketClient
 * @author: GTL
 * @create: 2019-01-22 09:56
 **/
public class SocketClient {

    public void sendSocket(String sendMsg) throws IOException {
        SocketChannel socketChannel=null;
        socketChannel=SocketChannel.open();

        SocketAddress socketAddress=new InetSocketAddress("127.0.0.1",8080);
        socketChannel.connect(socketAddress);
        ByteBuffer buffer=ByteBuffer.allocate(1024*1024);
        buffer.clear();
        buffer=buffer.put(sendMsg.getBytes("UTF-8"));

        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();

        //从服务端读取消息
        int readLenth=socketChannel.read(buffer);
        buffer.flip();
        byte[] bytes=new byte[readLenth];
        buffer.get(bytes);
        StringBuffer receiveMsg=new StringBuffer();
        receiveMsg.append(new String(bytes,"UTF-8"));
        buffer.clear();
        socketChannel.close();
    }
}
