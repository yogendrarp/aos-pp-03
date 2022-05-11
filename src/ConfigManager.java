public class ConfigManager {
    static Configurations getClientConfigurations(int id) {
        Configurations configurations = new Configurations();
        configurations.allDevClients = new String[]{"localhost:5001", "localhost:5002", "localhost:5003", "localhost:5004", "localhost:5005"};
        configurations.allProdClients = new String[]{"dc01.utdallas.edu:5000", "dc02.utdallas.edu:5000", "dc03.utdallas.edu:5000", "dc04.utdallas.edu:5000", "dc05.utdallas.edu:5000"};
        switch (id) {
            case 1 -> {
                configurations.devClients = new String[]{"localhost:5002", "localhost:5003"};
                configurations.devServers = new String[]{"localhost:6001", "localhost:6002", "localhost:6003"};
                configurations.myPort = 5001;
                configurations.prodClients = new String[]{"dc02.utdallas.edu:5000", "dc03.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 2 -> {
                configurations.devClients = new String[]{"localhost:5003", "localhost:5004"};
                configurations.devServers = new String[]{"localhost:6001", "localhost:6002", "localhost:6003"};
                configurations.myPort = 5002;
                configurations.prodClients = new String[]{"dc03.utdallas.edu:5000", "dc04.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 3 -> {
                configurations.devClients = new String[]{"localhost:5004", "localhost:5005"};
                configurations.devServers = new String[]{"localhost:6001", "localhost:6002", "localhost:6003"};
                configurations.myPort = 5003;
                configurations.prodClients = new String[]{"dc04.utdallas.edu:5000", "dc05.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 4 -> {
                configurations.devClients = new String[]{"localhost:5005", "localhost:5001"};
                configurations.devServers = new String[]{"localhost:6001", "localhost:6002", "localhost:6003"};
                configurations.myPort = 5004;
                configurations.prodClients = new String[]{"dc05.utdallas.edu:5000", "dc01.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
            case 5 -> {
                configurations.devClients = new String[]{"localhost:5001", "localhost:5002"};
                configurations.devServers = new String[]{"localhost:6001", "localhost:6002", "localhost:6003"};
                configurations.myPort = 5005;
                configurations.prodClients = new String[]{"dc01.utdallas.edu:5000", "dc02.utdallas.edu:5000"};
                configurations.prodServers = new String[]{"dc01.utdallas.edu:6000", "dc02.utdallas.edu:6000", "dc03.utdallas.edu:6000"};
            }
        }

        return configurations;
    }

    static ServerConfigurations getServerConfiguration(int serverId) {
        ServerConfigurations serverConfigurations = new ServerConfigurations();

        switch (serverId) {
            case 1:
                serverConfigurations.prodServerIp = "dc01.utdallas.edu";
                serverConfigurations.prodServerPort = 6000;
                serverConfigurations.devServerIp = "localhost";
                serverConfigurations.devServerPort = 6001;
                serverConfigurations.devPath="D:\\Code\\aos-pp-03\\Server1\\";
                serverConfigurations.prodPath="/home/012/y/yr/yrp200001/aospp3/Server1/";
                break;
            case 2:
                serverConfigurations.prodServerIp = "dc02.utdallas.edu";
                serverConfigurations.prodServerPort = 6000;
                serverConfigurations.devServerIp = "localhost";
                serverConfigurations.devServerPort = 6002;
                serverConfigurations.devPath="D:\\Code\\aos-pp-03\\Server2\\";
                serverConfigurations.prodPath="/home/012/y/yr/yrp200001/aospp3/Server2/";
                break;
            case 3:
                serverConfigurations.prodServerIp = "dc03.utdallas.edu";
                serverConfigurations.prodServerPort = 6000;
                serverConfigurations.devServerIp = "localhost";
                serverConfigurations.devServerPort = 6003;
                serverConfigurations.devPath="D:\\Code\\aos-pp-03\\Server3\\";
                serverConfigurations.prodPath="/home/012/y/yr/yrp200001/aospp3/Server3/";
                break;
        }
        return serverConfigurations;
    }
}
