package Classes;

import java.io.*;

public class Serializer {

    static byte[] encode(Object message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] encoded = null;

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(message);
            encoded = bos.toByteArray();
            bos.close();
        }
        catch (Exception e) {
            System.out.println("[ MessageSerializer ] Erro no encode da mensagem " + e);
        }

        return encoded;
    }

    static Object decode(byte[] message) {
        ByteArrayInputStream bis = new ByteArrayInputStream(message);
        Object decoded = null;

        try {
            ObjectInputStream in = new ObjectInputStream(bis);
            decoded = in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println("[ MessageSerializer ] Erro no decode da mensagem " + e);
        }

        return decoded;
    }

}
