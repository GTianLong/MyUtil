package socket;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @program: MyUtils
 * @description: socket服务端
 * @author: GTL
 * @create: 2019-01-22 09:22
 **/
@Component
public class SocketServer {

    //定义监听器
    private Selector selector;
    //定义socket端口
    private int listenPort;

    /*
    如果当前socketServer只是@Component，那么这个类里面的注解将无法使用，此时可以使用@PostConstruct解决。
    private static SocketServer socketServer;

    @PostConstruct
    private void init(){
        socketServer=this;
    }*/

    public void startServer(){

        //打开通信信道
        try {
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //获取嵌套字
            ServerSocket serverSocket=serverSocketChannel.socket();
            //绑定监听器
            serverSocket.bind(new InetSocketAddress(listenPort));
            //打开监听器
            selector=Selector.open();
            //将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true){
                //当注册事件到达时，方法返回，否则方法会一直阻塞
                selector.select();
                //获取selector中选中的迭代器，选中的为注册的事件
                Iterator<SelectionKey> keyIterator=this.selector.selectedKeys().iterator();
                while (keyIterator.hasNext()){
                    SelectionKey key=keyIterator.next();

                    if(key.isAcceptable()){
                        handle(key);
                    }

                    if(key.isReadable()){
                        readByte(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handle(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel=null;
        SocketChannel socketChannel=null;

        serverSocketChannel= (ServerSocketChannel) selectionKey.channel();
        socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        serverSocketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024*1024));
    }

    private void readByte(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel= (SocketChannel) selectionKey.channel();

        ByteBuffer buffer= (ByteBuffer) selectionKey.attachment();
        long byteLen=socketChannel.read(buffer);
        if(byteLen==-1){
            socketChannel.shutdownOutput();
            socketChannel.shutdownInput();
            socketChannel.close();
        }else {
            //将channel改为可读状态
            buffer.flip();
            //将字节转为UTF-8
            String receiveMsg= Charset.forName("UTF-8").decode(buffer).toString();
            buffer.clear();
            //对返回的字符串进行处理

            buffer=buffer.put(receiveMsg.getBytes("UTF-8"));
            buffer.flip();
            socketChannel.write(buffer);

            socketChannel.register(selectionKey.selector(),SelectionKey.OP_READ,ByteBuffer.allocate(1024*1024));

        }
    }
}
