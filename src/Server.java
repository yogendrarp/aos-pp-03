import java.net.ServerSocket;
import java.net.Socket;


public class Server {


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Need server id, for eg 1");
            System.exit(-1);
        }
        int serverId = Integer.parseInt(args[0]);
        ServerConfigurations serverConfigurations = ConfigManager.getServerConfiguration(serverId);
        try (ServerSocket server = new ServerSocket(serverConfigurations.devServerPort)) {
            System.out.println("Server " + serverId + " listening on " + serverConfigurations.devServerPort);
            while (true) {
                Socket client = server.accept();
                ClientHandler clientHandler = new ClientHandler(client, serverConfigurations.devPath);
                new Thread(clientHandler).start();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
