import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String path;

    public ClientHandler(Socket socket, String path
    ) {
        this.clientSocket = socket;
        this.path = path;
    }

    public void run() {
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            int length = 0;
            length = in.readInt();
            long clock = in.readLong();
            Random random = new Random();
            int randomNumber = random.nextInt((20 - 5) + 1) + 1;
            byte[] line = new byte[length];
            if (length > 0) {
                in.readFully(line);
                System.out.println(new String(line));
                if (randomNumber < 5) {
                    String _reJMessg = "ABORT";
                    out.writeLong(_reJMessg.length());
                    out.writeBytes(_reJMessg);

                } else {
                    String _comMessg = "COMMIT";
                    out.writeLong(_comMessg.length());
                    out.writeBytes(_comMessg);
                    length = 0;
                    length = in.readInt();
                    if (length > 0) {
                        line = new byte[length];
                        FileWriter.AppendToFile(path, new String(line));
                    }

                }
            }
        } catch (Exception ex) {

        }
    }
}
