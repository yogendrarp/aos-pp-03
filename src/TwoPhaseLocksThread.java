import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TwoPhaseLocksThread implements Runnable {


    String server;
    String msg;
    String fileName;
    Character[] status;
    int myIdx, othIdx1, othIdx2;

    public TwoPhaseLocksThread(String server, String msg, String fileName, Character[] status, int myIdx, int othIdx1, int othIdx2) {
        this.server = server;
        this.msg = msg;
        this.fileName = fileName;
        this.status = status;
        this.myIdx = myIdx;
        this.othIdx1 = othIdx1;
        this.othIdx2 = othIdx2;
    }

    @Override
    public void run() {
        String _server = server.split(":")[0];
        int port = Integer.parseInt(server.split(":")[1]);
        DataOutputStream dataOutputStream = null;
        DataInputStream in = null;
        try (Socket socket = new Socket(_server, port)) {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeInt(msg.length());
            dataOutputStream.writeBytes(msg);
            int responseLength = in.readInt();
            if (responseLength > 0) {
                byte[] responseBytes = new byte[responseLength];
                if (new String(responseBytes).equals("ABORT")) {
                    status[myIdx] = '1';
                } else if (new String(responseBytes).equals("COMMIT")) {
                    status[myIdx] = '2';
                    while (status[othIdx1] == 0 || status[othIdx2] == 0) {
                        //Wait Lock do nothing
                    }
                    if (status[othIdx1] == '2' && status[othIdx2] == '2') {
                        String comMsg = "COMMIT";
                        dataOutputStream.writeInt(comMsg.length());
                        dataOutputStream.writeBytes(comMsg);
                    } else {
                        String abortMsg = "ABORT";
                        dataOutputStream.writeInt(abortMsg.length());
                        dataOutputStream.writeBytes(abortMsg);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                dataOutputStream.close();
                in.close();
            } catch (Exception e) {

            }
        }
    }
}
