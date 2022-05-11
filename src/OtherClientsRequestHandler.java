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
    int clientId;
    ArrayList<Quorum> fileQuorums;

    public OtherClientsRequestHandler(Socket socket, ArrayList<PriorityQueue<Message>> queue, String filesInfo,
                                      LamportsClock lamportsClock, HashSet<String> requests, String path,
                                      Character[] votes, Configurations configurations,
                                      int clientId, ArrayList<Quorum> fileQuorums) {
        this.clientSocket = socket;
        this.requestQueues = queue;
        this.filesInfo = filesInfo;
        this.lamportsClock = lamportsClock;
        this.requests = requests;
        this.path = path;
        this.votes = votes;
        this.configurations = configurations;
        this.clientId = clientId;
        this.fileQuorums = fileQuorums;
    }

    public void run() {
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            int length = 0;
            length = in.readInt();
            byte[] line = new byte[length];
            if (length > 0) {
                in.readFully(line);
                String[] messageTokens = new String(line).split("#");
                String msgType = messageTokens[0];
                String _sclientId = messageTokens[1];
                char clientIdChar = _sclientId.charAt(0);
                int clientIdx = Integer.parseInt(_sclientId);
                String fileName = messageTokens[2];
                int number = Integer.parseInt(fileName.replaceAll("[^\\d]", " ").trim());
                // Handle enquiry, send hosted file information
                if (msgType.equals("ENQUIRY") && votes[number - 1] == '0') {
                    System.out.println("Giving vote");
                    votes[number - 1] = clientIdChar;
                    out.writeInt(5);
                    out.writeBytes("REPLY");
                } else {
                    //Send yield message to who you have voted
                    String clientInfo = configurations.allProdClients[clientIdx - 1];
                    String server = clientInfo.split(":")[0];
                    int port = Integer.parseInt(clientInfo.split(":")[1]);
                    try (Socket yieldSocket = new Socket(server, port)) {
                        DataOutputStream outSendYield = new DataOutputStream(yieldSocket.getOutputStream());
                        DataInputStream inSendYield = new DataInputStream(yieldSocket.getInputStream());
                        String yieldMsg = "YIELD#" + clientId + "#" + fileName;
                        outSendYield.writeInt(yieldMsg.length());
                        outSendYield.writeBytes(yieldMsg);
                        Thread.sleep(2000);
                        out.writeInt(5);
                        out.writeBytes("REPLY");
                    } catch (Exception e) {
                    }
                }
                if (msgType.equals("YIELD")) {
                    //then from your quorum set remove the locks obtained.
                    for (int i = 0; i < fileQuorums.size(); i++) {
                        if (fileQuorums.get(i).fileName.equals(fileName)) {
                            if (dirtyLogic(clientId, clientIdx) == 1) {
                                fileQuorums.get(i).vote1 = false;
                            } else if (dirtyLogic(clientId, clientIdx) == 2) {
                                fileQuorums.get(i).vote2 = false;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {

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

    private int dirtyLogic(int clientId, int otherId) {
        switch (clientId) {
            case 1:
                if (otherId == 2) {
                    return 1;
                } else {
                    return 2;
                }
            case 2:
                if (otherId == 3) {
                    return 1;
                } else {
                    return 2;
                }
            case 3:
                if (otherId == 4) {
                    return 1;
                } else {
                    return 2;
                }
            case 4:
                if (otherId == 5) {
                    return 1;
                } else {
                    return 2;
                }
            case 5:
                if (otherId == 1) {
                    return 1;
                } else {
                    return 2;
                }
        }
        return 0;
    }
}
