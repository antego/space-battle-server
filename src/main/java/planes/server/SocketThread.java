package planes.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 29.12.15.
 */
public class SocketThread extends Thread {
    private static final Logger logger = Logger.getLogger(SocketThread.class.getName());
    private final Socket sourceSocket;
    private final Set<SocketThread> socketThreadSet;
    private final Object pairedThreadLock = new Object();
    private SocketThread pairedThread;

    public SocketThread(Socket sourceSocket, Set<SocketThread> socketThreadSet) {
        this.sourceSocket = sourceSocket;
        this.socketThreadSet = socketThreadSet;
    }

    @Override
    public void run() {
        try (Socket socket = this.sourceSocket) {
            if(!ProtocolUtils.doHandshake(socket)) {
                return;
            }
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                synchronized (pairedThreadLock) {
                    while (pairedThread == null) {
                        pairedThreadLock.wait();
                    }
                }
                ProtocolUtils.processMessage(socket.getInputStream(), pairedThread.getSocket().getOutputStream());
                //TODO shutdown thread on pair thread stop
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "exception in socket thread", e);
        } catch (InterruptedException e) {

        }
        deleteFromThreadSet();
    }

    private void deleteFromThreadSet() {
        synchronized (socketThreadSet) {
            socketThreadSet.remove(this);
        }
    }

    public void registerPairedThread(SocketThread pairedThread) {
        if (pairedThread == null) {
            return;
        }
        synchronized (pairedThreadLock) {
            this.pairedThread = pairedThread;
            pairedThreadLock.notify();
        }
    }

    public Socket getSocket() {
        return sourceSocket;
    }

    public void closeSocket() throws IOException {
        if(!sourceSocket.isClosed()) {
            sourceSocket.close();
        }
    }
}
