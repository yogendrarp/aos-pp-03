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
            Random random = new Random();
            int randomNumber = random.nextInt((20 - 5) + 1) + 1;
            byte[] line = new byte[length];
            if (length > 0) {
                in.readFully(line);
                System.out.println("Msg recieved is : " + new String(line));
                String[] tokens = new String(line).split("#");
                String clientId = tokens[0];
                String fileName = tokens[1];
                String lamportsClock = tokens[2];
                String city = tokens[3];
                String formedMsg = clientId + ", " + lamportsClock + ", " + city;
                System.out.println("Random number generated is" + randomNumber);
                if (randomNumber < 2) {
                    System.out.println("Request will be aborted");
                    String _reJMessg = "ABORT";
                    out.writeInt(_reJMessg.length());
                    out.writeBytes(_reJMessg);
                } else {
                    System.out.println("Request will be committed");
                    String _comMessg = "COMMIT";
                    out.writeInt(_comMessg.length());
                    out.writeBytes(_comMessg);
                    length = 0;
                    length = in.readInt();
                    if (length > 0) {
                        line = new byte[length];
                        in.readFully(line);
                        System.out.println("Msg obtained " + new String(line));
                        //Check if commit else abort
                        if (new String(line).equals("COMMIT")) {
                            System.out.println(path + " " + fileName + " " + formedMsg);
                            FileWriter.AppendToFile(path + fileName, formedMsg);
                        } else {
                            System.out.println("Aborting Requests!");
                        }
                    }

                }
            }
        } catch (Exception ex) {

        }
    }
}
