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

    public OtherClientsRequestHandler(Socket socket, ArrayList<PriorityQueue<Message>> queue, String filesInfo, LamportsClock lamportsClock, HashSet<String> requests, String path) {
        this.clientSocket = socket;
        this.requestQueues = queue;
        this.filesInfo = filesInfo;
        this.lamportsClock = lamportsClock;
        this.requests = requests;
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
            byte[] line = new byte[length];
            if (length > 0) {
                in.readFully(line);
                System.out.println(new String(line));
                String[] messageTokens = new String(line).split("#");
                // Handle enquiry, send hosted file information
                if (messageTokens[0].equals("WRITE")) {
                    Message msg = new Message();
                    msg.type = "WRITE";
                    msg.clientId = Integer.parseInt(messageTokens[1]);
                    msg.timeStamp = Long.parseLong(messageTokens[2]);
                    msg.message = messageTokens[3];
                    msg.fileName = messageTokens[4];
                    //Get the file name where it has to be written and add to appropriate queue
                    int idx = getIndexOfFile(msg.fileName, filesInfo);
                    requestQueues.get(idx).add(msg);
                    System.out.println("Received msg from " + msg);

                    lamportsClock.clockValue=clock;

                    lamportsClock.clockValue++;
                    boolean flag = true;
                    //Use hashset to understand if the request has been processed from the queue, if yes send ack to client
                    while (flag) {
                        boolean containsData = requests.contains("c:" + msg.clientId + ",f:" + msg.fileName + ",t:" + msg.timeStamp);
                        if (containsData) {
                            flag = false;
                            requests.remove("c:" + msg.clientId + ",f:" + msg.fileName + ",t:" + msg.timeStamp);
                        }
                    }
                    System.out.println("Coming out now, its processed");
                    String successMsg = "SUCCESS";
                    out.writeInt(successMsg.length());
                    out.writeBytes(successMsg);
                }//Manage requests that come from other servers, i.e proxies
                else if (messageTokens[0].equals("SERVER")) {
                    Message msg = new Message();
                    msg.type = "SERVER";
                    msg.clientId = Integer.parseInt(messageTokens[1]);
                    msg.timeStamp = Long.parseLong(messageTokens[2]);
                    msg.message = messageTokens[3];
                    msg.fileName = messageTokens[4];
                    int idx = getIndexOfFile(msg.fileName, filesInfo);
                    requestQueues.get(idx).add(msg);
                    lamportsClock.clockValue++;
                    boolean flag = true;
                    while (flag) {
                        boolean containsData = requests.contains("c:" + msg.clientId + ",f:" + msg.fileName + ",t:" + msg.timeStamp);
                        if (containsData) {
                            //Send request back to the stream and enquire if it obtained a lock from other server and then write to file.
                            System.out.println("Other Server request can be processed, handing over the lock");
                            String successMsg = "LOCK";
                            out.writeInt(successMsg.length());
                            out.writeBytes(successMsg);
                            long lcClock;
                            while (true) {
                                length = 0;
                                length = in.readInt();
                                lcClock = in.readLong();

                                lamportsClock.clockValue=++lcClock;

                                if (length > 0) {
                                    byte[] successmsg = new byte[length];
                                    in.readFully(successmsg);
                                    System.out.println(new String(successmsg));
                                    break;
                                }
                            }
                            requests.remove("c:" + msg.clientId + ",f:" + msg.fileName + ",t:" + msg.timeStamp);
                        }
                    }
                }//If the proxy server has acquired locks, then it sends this as final write, so write directly
                else if (messageTokens[0].equals("FINALWRITE")) {
                    Message msg = new Message();
                    msg.type = "FINALWRITE";
                    msg.clientId = Integer.parseInt(messageTokens[1]);
                    msg.timeStamp = Long.parseLong(messageTokens[2]);
                    msg.message = messageTokens[3];
                    msg.fileName = messageTokens[4];

                    System.out.println("Other Server request has been processed");
                    System.out.println("**** " + msg);
                    //Append to the file as part of sync
                    //FileWriter.AppendToFile(path + msg.fileName, msg.clientId + ", " + msg.timeStamp + ", " + msg.message);
                    String successMsg = "WRITTEN_ACK";
                    out.writeInt(successMsg.length());
                    out.writeBytes(successMsg);
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
