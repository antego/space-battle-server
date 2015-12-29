package planes.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 * Created by anton on 29.12.15.
 */
public class Server {
    public static final String SEP=System.lineSeparator();
    AcceptThread acceptThread;

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    public Server() throws IOException {
        boolean exit = false;
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        while (!exit) {
            showHint();
            String command;
            while ((command = consoleReader.readLine()) != null) {
                switch (command) {
                    case "start":
                        if(acceptThread == null) {
                            start();
                            System.out.println("server started" + SEP);
                        } else {
                            System.out.println("already started" + SEP);
                        }
                        break;
                    case "stop":
                        if (acceptThread != null) {
                            stop();
                            System.out.println("server stopped" + SEP);
                        } else {
                            System.out.println("nothing to stop" + SEP);
                        }
                        break;
                    default:
                        showHint();
                }
            }
        }
    }

    private void stop() {
        acceptThread.closeAllSockets();
    }

    private void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9998);
        acceptThread = new AcceptThread(serverSocket);
        acceptThread.start();
    }

    private void showHint() {
        System.out.println("Print <start> to start server, or <stop> to stop." + SEP);
    }
}
