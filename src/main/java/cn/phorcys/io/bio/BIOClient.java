package cn.phorcys.io.bio;

import java.io.IOException;
import java.net.Socket;

public class BIOClient {
    private static int THREAD_TEST_COUNT = 5;

    public static void main(String[] args) {
//        run();
        multiThreadRun();
    }

    private static void multiThreadRun() {
        int i = 0;
        while (i < THREAD_TEST_COUNT) {
            new Thread(BIOClient::run).start();
            i++;
        }
    }

    private static void run() {
        try {
            Socket c = new Socket("127.0.0.1", 8000);
            c.getOutputStream().write("我们在哪儿见过你知道吗？".getBytes());
            c.getOutputStream().flush();

            System.out.println("Already send , wait response");

            byte[] bytes = new byte[1024];
            int len = c.getInputStream().read(bytes);
            System.out.println(new String(bytes, 0, len));
            c.close();
        } catch (IOException e) {
            System.out.println("建立socket连接失败！");
            e.printStackTrace();
        }
    }
}
