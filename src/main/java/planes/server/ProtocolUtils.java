package planes.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by anton on 29.12.15.
 */
public class ProtocolUtils {
    private static final Integer MESSAGE_LENGTH = 4;

    public static void processMessage(Socket masterSocket, Socket slaveSocket, SessionContext context) throws IOException {
        switch (context.getPhase()) {
            case SETUP_WORLD:
                setupWorld(masterSocket, context);
                context.setPhase(SessionContext.SessionPhase.GAME);
                break;
            case GAME:
                byte[] message = readMessage(masterSocket.getInputStream());
                message = doBusiness(message);
                writeMessage(slaveSocket.getOutputStream(), message);
        }
    }

    private static void setupWorld(Socket masterSocket, SessionContext context) throws IOException {
        masterSocket.getOutputStream().write(context.getPlayerSide() == SessionContext.PlayerSide.LEFT ? new byte[]{0} : new byte[]{1});
    }

    private static void writeMessage(OutputStream outputStream, byte[] message) throws IOException {
        outputStream.write(message);
    }

    private static byte[] doBusiness(byte[] message) {
        return message;
    }

    private static byte[] readMessage(InputStream inputStream) throws IOException {
        byte[] message = new byte[MESSAGE_LENGTH];
        int len = inputStream.read(message);
        if (len == -1) {
            //todo exit without exception
            throw new EOFException();
        }
        while (len < MESSAGE_LENGTH) {
            len += inputStream.read(message, len, MESSAGE_LENGTH - len);
        }
        System.out.println(len);
        return message;
    }

    //TODO proper handshake
    public static boolean doHandshake(Socket socket) {
        return true;
    }
}
