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
    int clientId;
    ArrayList<Quorum> fileQuorums;


    public ServerOfClient(String filesInfo, ServerSocket server, ArrayList<PriorityQueue<Message>> requestQueues,
                          LamportsClock lamportsClock, HashSet<String> requests, String path,
                          Configurations configurations, Character[] votes, int clientId,
                          ArrayList<Quorum> fileQuorums) {
        this.filesInfo = filesInfo;
        this.server = server;
        this.requestQueues = requestQueues;
        this.lamportsClock = lamportsClock;
        this.requests = requests;
        this.path = path;
        this.configurations = configurations;
        this.votes = votes;
        this.clientId = clientId;
        this.fileQuorums = fileQuorums;
    }

    public void run() {
        try {
            server = new ServerSocket(configurations.prodPort);
            System.out.println("Client 1 listens for requests on " + configurations.prodPort);
            server.setReuseAddress(true);
            while (true) {
                Socket client = server.accept();
                OtherClientsRequestHandler clientHandler = new OtherClientsRequestHandler(client, requestQueues,
                        filesInfo, lamportsClock, requests, path,
                        votes, configurations, clientId, fileQuorums);
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
