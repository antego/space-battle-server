package planes.client;

import planes.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by anton on 29.12.2015.
 */
public class Client {
    public static final String HOST = "127.0.0.1";
    public static final Integer PORT = 9998;
    private static final Integer MESSAGE_COUNT = 100;
    ExecutorService receiverExecutor = Executors.newSingleThreadExecutor();
    Socket senderSocket;
    Socket receiverSocket;

    public Client() throws IOException {
        senderSocket = new Socket(HOST, PORT);
        receiverSocket = new Socket(HOST, PORT);
    }

    private void startTest() throws InterruptedException, IOException, ExecutionException {
        new Thread(() -> {
            for(int i = 0; i < MESSAGE_COUNT; i++) {
                try {
                    new DataOutputStream(senderSocket.getOutputStream()).writeDouble(12321.123);
                    Thread.sleep(1000/30);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Future<Integer> times = receiverExecutor.submit(() -> {
            int i = 0;
            DataInputStream dataInputStream = new DataInputStream(receiverSocket.getInputStream());
            while (i < MESSAGE_COUNT) {
                if(i != 0 && i % 100 == 0) {
                    System.out.println(i + "" + Server.SEP);
                }
                try {
                    System.out.println(dataInputStream.readDouble());
                } catch (IOException e) {
                    System.out.println("connection halted, received " + i + " packets");
                    break;
                }
                i++;
            }
            return i;
        });

        try {
            System.out.println("received " + times.get(65, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
        }
        receiverExecutor.shutdown();
        senderSocket.close();
        receiverSocket.close();
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Client client = new Client();
        client.startTest();
    }
}
