import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Client {
    static ArrayList<String> files = new ArrayList<>(Arrays.asList("F1.txt", "F2.txt", "F3.txt", "F4.txt", "F5.txt", "F6.txt", "F7.txt", "F8.txt"));
    static Character[] votes = new Character[]{'0', '0', '0', '0', '0', '0', '0', '0'};
    static String[] servers;
    static String[] clients;
    static ArrayList<Quorum> fileQuorums = new ArrayList<Quorum>();
    static HashSet<String> requests = new HashSet<>();
    static String citiesFile = "citiestexas.txt";
    static String path = "D:\\Code\\aos-pp-03\\";
    static ArrayList<PriorityQueue<Message>> requestQueues = new ArrayList<PriorityQueue<Message>>();
    static LamportsClock lamportsClock = new LamportsClock();

    public static void main(String[] args) throws IOException, InterruptedException {

        //server will listen to incoming requests
        ServerSocket server = null;

        ClockComparator clockComparator = new ClockComparator();
        if (args.length < 1) {
            System.out.println("Need client id ex 1");
            System.exit(-1);
        }

        int clientId = Integer.parseInt(args[0]);
        Configurations configurations = ConfigManager.getClientConfigurations(clientId);

        servers = configurations.prodServers;
        clients = configurations.prodClients;

        List<String> cities = Files.readAllLines(Paths.get(path + citiesFile));
        for (int i = 0; i < files.size(); i++) {
            fileQuorums.add(new Quorum(files.get(i), i));
            requestQueues.add(new PriorityQueue<>(clockComparator));
        }
        int filesSize = files.size();
        String filesInfo = files.stream().map(Object::toString).collect(Collectors.joining(","));
        ServerOfClient serverOfClient = new ServerOfClient(filesInfo, server, requestQueues, lamportsClock, requests, path, configurations, votes, clientId, fileQuorums);
        Thread serverThread = new Thread(serverOfClient);
        serverThread.start();

        System.out.println("Waiting for all servers and clients to be up");
        for (String serv : servers) {
            System.out.println("Checking if " + serv + " is up!");
            while (true) {
                String ip = serv.split(":")[0];
                int port = Integer.parseInt(serv.split(":")[1]);
                if (checkIfAlive(ip, port)) {
                    break;
                }
                Thread.sleep(1000);
            }
            System.out.println(serv + " is up!");
        }
        for (String client : clients) {
            System.out.println("Checking if " + client + " is up!");
            while (true) {
                String ip = client.split(":")[0];
                int port = Integer.parseInt(client.split(":")[1]);
                if (checkIfAlive(ip, port)) {
                    break;
                }
                Thread.sleep(1000);
            }
            System.out.println(client + " is up!");
        }
        System.out.println("All clients are up!");

       /* if (clientId > 1) {
            return;
        }*/

        for (int i = 0; i < 20; i++) {
            int randomIndex = new Random().nextInt((filesSize));
            int randomCityIndex = new Random().nextInt(cities.size() - 1);
            String randomCity = cities.get(randomCityIndex);
            String msg = "ENQUIRE#" + clientId + "#" + files.get(randomIndex);
            String _wmsg = clientId + "#" + files.get(randomIndex) + "#" + lamportsClock.clockValue + "#" + randomCity;
            Quorum quorum = fileQuorums.get(randomIndex);
            ObtainBothQuorum obtainBothQuorum = new ObtainBothQuorum(msg, clients, lamportsClock.clockValue++, quorum);
            obtainBothQuorum.obtain();
            if (quorum.vote1 && quorum.vote2) {
                System.out.println("Obtained locks, proceeding to two phase locking protocol");
                TwoPhaseLockHandler _twoPhaseLockHandler = new TwoPhaseLockHandler(servers, files.get(randomIndex), _wmsg);
                _twoPhaseLockHandler.run();
            }

            Thread.sleep(new Random().nextInt(4) * 1000);
        }
    }


    //Lamports clock is updated with curr+1 and clock from server
    private static void updateLamportsClock(long val) {
        lamportsClock.clockValue++;
        lamportsClock.clockValue = Math.max(val, lamportsClock.clockValue);
    }

    private static boolean checkIfAlive(String server, int port) {
        try {
            Socket socket = new Socket(server, port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
