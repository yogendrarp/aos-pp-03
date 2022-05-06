import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ObtainQuorum implements Runnable {

    String msg;
    String server;
    long lamportsClock;
    Quorum quorum;
    int idx;


    public ObtainQuorum(String msg, String server, long lamportsClock, Quorum quorum,int index) {
        this.msg = msg;
        this.server = server;
        this.lamportsClock = lamportsClock;
        this.quorum = quorum;
        this.idx=index;
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
            dataOutputStream.writeLong(lamportsClock);
            dataOutputStream.writeBytes(msg);

            while (true) {
                /**
                 *Connect to one of the servers and inform that it has to write
                 * once approved, send the facilitate that it has acquired both the servers permission
                 */
                int length = in.readInt();
                System.out.println("Waiting for the response");
                if (length > 0) {
                    byte[] successMsg = new byte[length];
                    in.readFully(successMsg);
                    System.out.println(new String(successMsg));
                    System.out.println("Obtained lock from" + server + " " + port);
                    if(idx==0)
                        quorum.vote1=true;
                    if(idx==1)
                        quorum.vote2=true;
                    break;
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                dataOutputStream.close();
                in.close();
            } catch (Exception e) {

            }
        }
    }
}
