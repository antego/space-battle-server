package planes.server;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by anton on 29.12.15.
 */
public class ProtocolUtils {
    public static void processMessage(InputStream inputStream, OutputStream outputStream) {
        byte[] message = readMessage(inputStream);
        message = doBuisness(message);
        writeMessage(outputStream, message);
    }

    private static void writeMessage(OutputStream outputStream, byte[] message) {
        try {
            outputStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] doBuisness(byte[] message) {
        return message;
    }

    private static byte[] readMessage(InputStream inputStream) {
        byte[] message = new byte[4];
        try {
            inputStream.read(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}
