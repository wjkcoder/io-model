package cn.phorcys.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOThreadServer {
    private ExecutorService threadPool = Executors.newFixedThreadPool(50);
    private Selector selector;
    private static final int SIZE = 1024;

    private void serverInit() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
        this.selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIOThreadServer started at " +
                ssc.socket().getInetAddress() + " " +
                ssc.socket().getLocalPort());

    }

    private void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            threadPool.execute(new Threadhandle(key));


        }
    }

    public static void main(String[] args) {
        try {
            NIOThreadServer server = new NIOThreadServer();
            server.serverInit();
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void listen() throws IOException {
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
    }

    private class Threadhandle extends Thread {
        private SelectionKey key;

        Threadhandle(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(SIZE);
            int len = 0;
            try {
                len = sc.read(buffer);

                String content = len != -1 ? new String(buffer.array(), 0, len) : "";
                System.out.println(content);
                String sb = "Stream has been receive successfully!" +
                        content;
                sc.write(ByteBuffer.wrap(sb.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            buffer.flip();

        }
    }
}
