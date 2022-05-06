import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Client handler implements Runnable to ensure, server can cater to n number of clients
 */
public class OtherClientsRequestHandler implements Runnable {
    private final Socket clientSocket;
    //Add to queue respective of the file
    private final ArrayList<PriorityQueue<Message>> requestQueues;
    private final String filesInfo;
    private final LamportsClock lamportsClock;
    //Hashset holds all processed requests
    private final HashSet<String> requests;
    private final String path;
    Character[] votes;
    Configurations configurations;

    public OtherClientsRequestHandler(Socket socket, ArrayList<PriorityQueue<Message>> queue, String filesInfo, LamportsClock lamportsClock, HashSet<String> requests, String path, Character[] votes, Configurations configurations) {
        this.clientSocket = socket;
        this.requestQueues = queue;
        this.filesInfo = filesInfo;
        this.lamportsClock = lamportsClock;
        this.requests = requests;
        this.path = path;
        this.votes = votes;
        this.configurations = configurations;
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
            byte[] line = new byte[length];
            if (length > 0) {
                in.readFully(line);
                System.out.println(new String(line));
                String[] messageTokens = new String(line).split("#");
                String msgType = messageTokens[0];
                String clientId = messageTokens[1];
                Character clientIdChar = clientId.charAt(0);
                Integer clientIdx = Integer.parseInt(clientId);
                String fileName = messageTokens[2];
                int number = Integer.parseInt(fileName.replaceAll("[^\\d]", " ").trim());
                // Handle enquiry, send hosted file information
                if (messageTokens[0].equals("ENQUIRY") && votes[number - 1] == '0') {
                    System.out.println("Giving vote");
                    votes[number - 1] = clientIdChar;
                    out.writeLong(5);
                    out.writeBytes("VOTED");
                } else {
                    //Send yeild message to who you have voted
                    String clientInfo = configurations.allDevClients[clientIdx - 1];

                }
                if (messageTokens[0].equals("YIELD")) {
                    //then from your quorum set remove the locks obtained.
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * Get Index of the file from filename
     *
     * @param fileName
     * @param filesInfo
     * @return index
     */
    private int getIndexOfFile(String fileName, String filesInfo) {
        String[] split = filesInfo.split(",");
        int index = 0;
        for (String s : split) {
            if (s.equalsIgnoreCase(fileName)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
