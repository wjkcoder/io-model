package cn.phorcys.io.nio;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
            ssc.configureBlocking(false);

            System.out.println("Socket server in single pool mode started,listening on:" + ssc.getLocalAddress());
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    handle(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(SelectionKey key) {
        if (key.isAcceptable()) {
            // 判断key是accept，注册一个read key
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            try {
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(key.selector(), SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.clear();
            try {
                int len = sc.read(buffer);
                String content = len != -1 ? new String(buffer.array(), 0, len) : "";

                ByteBuffer writeBuffer = ByteBuffer.wrap(("Response from server:" + content).getBytes());

                sc.write(writeBuffer);

            } catch (IOException e) {

                e.printStackTrace();
            } finally {
                if (sc != null) {
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
