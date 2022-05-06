import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Client {

    Configurations configurations;
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
        System.out.println("Sleeping for 5 seconds to ensure all clients are up!");
        Thread.sleep(5000);

        int clientId = Integer.parseInt(args[0]);
        Configurations configurations = ConfigManager.getConfigurations(clientId);

        servers = configurations.devServers;
        clients = configurations.devClients;

        List<String> cities = Files.readAllLines(Paths.get(path + citiesFile));
        for (int i = 0; i < files.size(); i++) {
            fileQuorums.add(new Quorum(files.get(i), i));
            requestQueues.add(new PriorityQueue<>(clockComparator));
        }
        int filesSize = files.size();
        String filesInfo = files.stream().map(Object::toString).collect(Collectors.joining(","));
        System.out.println("Before listening");
        ServerOfClient serverOfClient = new ServerOfClient(filesInfo, server, requestQueues, lamportsClock, requests, path, configurations, votes, clientId, fileQuorums);
        Thread serverThread = new Thread(serverOfClient);
        serverThread.start();
        System.out.println("After listening");
        for (int i = 0; i < 20; i++) {
            int randomIndex = new Random().nextInt((filesSize));
            int randomCityIndex = new Random().nextInt(cities.size() - 1);
            String randomCity = cities.get(randomCityIndex);
            String msg = "ENQUIRE#" + clientId + "#" + files.get(randomIndex);
            Quorum quorum = fileQuorums.get(randomIndex);
            ObtainBothQuorum obtainBothQuorum = new ObtainBothQuorum(msg, clients, lamportsClock.clockValue, quorum);
            obtainBothQuorum.obtain();
            if (quorum.vote1 && quorum.vote2) {
                System.out.println("Obtained locks, proceed to 2 PL");
            }
        }
    }


    //Lamports clock is updated with curr+1 and clock from server
    private static void updateLamportsClock(long val) {
        lamportsClock.clockValue++;
        lamportsClock.clockValue = Math.max(val, lamportsClock.clockValue);
    }

}
