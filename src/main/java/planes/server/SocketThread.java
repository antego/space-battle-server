package planes.server;

import java.io.EOFException;
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
    private final Object pairedThreadLock = new Object();
    private volatile SocketThread pairedThread;

    private SessionContext context;

    private Set<SocketThread> socketThreadSet;
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
                ProtocolUtils.processMessage(context);
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "exception in socket thread", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closePairedThread();
        deleteFromThreadSet();
    }

    public SessionContext getContext() {
        return context;
    }

    public void setContext(SessionContext context) {
        this.context = context;
    }

    private void deleteFromThreadSet() {
        synchronized (socketThreadSet) {
            socketThreadSet.remove(this);
        }
    }

    private void closePairedThread() {
        if (pairedThread != null) {
            try {
                pairedThread.closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pairedThread = null;
        }
    }

    public void registerPairedThread(SocketThread pairedThread) {
        if (pairedThread == null) {
            return;
        }
        synchronized (pairedThreadLock) {
            this.pairedThread = pairedThread;
            context.setSlaveThread(pairedThread);
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

    public boolean isClientAvaible() throws IOException {
        int len = sourceSocket.getInputStream().read(new byte[0]);
        return len != -1;
    }
}
