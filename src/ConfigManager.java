public class ConfigManager {
    static Configurations getConfigurations(int id) {
        Configurations configurations = new Configurations();

        switch (id) {
            case 1 -> {
                configurations.devClients = new String[]{"localhost:5002", "localhost:5003"};
                configurations.devServers = new String[]{"localhost:6000", "localhost:6001", "localhost:6002"};
                configurations.myPort = 5001;
                configurations.prodClients = new String[]{"dc02.utdallas.edu:5000", "dc03.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 2 -> {
                configurations.devClients = new String[]{"localhost:5003", "localhost:5004"};
                configurations.devServers = new String[]{"localhost:6000", "localhost:6001", "localhost:6002"};
                configurations.myPort = 5002;
                configurations.prodClients = new String[]{"dc03.utdallas.edu:5000", "dc04.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 3 -> {
                configurations.devClients = new String[]{"localhost:5004", "localhost:5005"};
                configurations.devServers = new String[]{"localhost:6000", "localhost:6001", "localhost:6002"};
                configurations.myPort = 5003;
                configurations.prodClients = new String[]{"dc04.utdallas.edu:5000", "dc05.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 4 -> {
                configurations.devClients = new String[]{"localhost:5005", "localhost:5001"};
                configurations.devServers = new String[]{"localhost:6000", "localhost:6001", "localhost:6002"};
                configurations.myPort = 5004;
                configurations.prodClients = new String[]{"dc05.utdallas.edu:5000", "dc01.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 5 -> {
                configurations.devClients = new String[]{"localhost:5001", "localhost:5002"};
                configurations.devServers = new String[]{"localhost:6000", "localhost:6001", "localhost:6002"};
                configurations.myPort = 5005;
                configurations.prodClients = new String[]{"dc01.utdallas.edu:5000", "dc02.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
        }

        return configurations;
    }
}
