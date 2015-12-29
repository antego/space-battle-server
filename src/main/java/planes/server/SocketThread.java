package planes.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 29.12.15.
 */
public class SocketThread extends Thread {
    private static final Logger logger = Logger.getLogger(SocketThread.class.getName());
    private final Socket sourceSocket;
    private SocketThread pairedThread;
    private final Object pairedThreadLock = new Object();

    public SocketThread(Socket sourceSocket) {
        this.sourceSocket = sourceSocket;
    }

    @Override
    public void run() {
        try (Socket socket = this.sourceSocket) {
            if(!ProtocolUtils.doHandshake(socket.)) {
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (pairedThreadLock) {
                    if (pairedThread == null) {
                        pairedThread.wait();
                    }
                }
                ProtocolUtils.processMessage(socket.getInputStream(), pairedThread.getSocket().getOutputStream());
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "exception in socket thread", e);
        } catch (InterruptedException e) {

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
        sourceSocket.close();
    }
}
