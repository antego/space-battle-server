package planes.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 29.12.15.
 */
public class AcceptThread extends Thread {
    public static final Logger logger = Logger.getLogger(AcceptThread.class.getName());
    private final ServerSocket serverSocket;
    private final Set<SocketThread> socketThreadSet = new HashSet<>();
    private SocketThread unpairedThread;

    public AcceptThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                SocketThread socketThread = new SocketThread(socket, socketThreadSet);
                socketThread.start();

                synchronized (socketThreadSet) {
                    socketThreadSet.add(socketThread);
                }

                if(unpairedThread == null) {
                    unpairedThread = socketThread;
                } else {
                    unpairedThread.registerPairedThread(socketThread);
                    socketThread.registerPairedThread(unpairedThread);
                    unpairedThread = null;
                }
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Exception in accept thread", e);
        }
    }

    public void closeAllSockets() {
        synchronized (socketThreadSet) {
            socketThreadSet.stream().forEach((thread) -> {
                try {
                    thread.closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
