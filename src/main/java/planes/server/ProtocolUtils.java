package planes.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by anton on 29.12.15.
 */
public class ProtocolUtils {
    private static final Integer MESSAGE_LENGTH = 4;
    public static void processMessage(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] message = readMessage(inputStream);
        message = doBusiness(message);
        writeMessage(outputStream, message);
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
        while (len < MESSAGE_LENGTH) {
            len += inputStream.read(message, len, MESSAGE_LENGTH - len);
        }
        System.out.println(len + Server.SEP);
        return message;
    }

    //TODO proper handshake
    public static boolean doHandshake(Socket socket) {
        return true;
    }
}
