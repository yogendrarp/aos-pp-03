import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class ServerOfClient implements Runnable {

    String filesInfo;
    ServerSocket server;
    ArrayList<PriorityQueue<Message>> requestQueues;
    LamportsClock lamportsClock;
    HashSet<String> requests;
    String path;
    Configurations configurations;
    Character[] votes;

    public ServerOfClient(String filesInfo, ServerSocket server, ArrayList<PriorityQueue<Message>> requestQueues,
                          LamportsClock lamportsClock, HashSet<String> requests, String path, Configurations configurations,Character[] votes) {
        this.filesInfo = filesInfo;
        this.server = server;
        this.requestQueues = requestQueues;
        this.lamportsClock = lamportsClock;
        this.requests = requests;
        this.path = path;
        this.configurations = configurations;
        this.votes=votes;
    }

    public void run() {
        try {
            server = new ServerSocket(configurations.myPort);
            System.out.println("Client 1 listens for requests on " + configurations.myPort);
            server.setReuseAddress(true);
            while (true) {
                Socket client = server.accept();
                OtherClientsRequestHandler clientHandler = new OtherClientsRequestHandler(client, requestQueues, filesInfo, lamportsClock, requests, path, votes);
                new Thread(clientHandler).start();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
