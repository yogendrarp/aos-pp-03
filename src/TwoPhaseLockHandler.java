import java.util.ArrayList;

public class TwoPhaseLockHandler {

    String[] servers;
    String msg;
    String fileName;
    Character[] status = new Character[]{'0','0','0'};


    public TwoPhaseLockHandler(String[] servers, String fileName, String msg) {
        this.servers = servers;
        this.msg = msg;
        this.fileName = fileName;
    }

    public void run() throws InterruptedException {
        TwoPhaseLocksThread _serverPhase1 = new TwoPhaseLocksThread(servers[0], msg, fileName, status, 0, 1, 2);
        TwoPhaseLocksThread _serverPhase2 = new TwoPhaseLocksThread(servers[1], msg, fileName, status, 1, 0, 2);
        TwoPhaseLocksThread _serverPhase3 = new TwoPhaseLocksThread(servers[1], msg, fileName, status, 2, 0, 1);
        Thread _serverThread1 = new Thread(_serverPhase1);
        Thread _serverThread2 = new Thread(_serverPhase2);
        Thread _serverThread3 = new Thread(_serverPhase3);

        _serverThread1.start();
        _serverThread2.start();
        _serverThread3.start();

        _serverThread1.join();
        _serverThread2.join();
        _serverThread3.join();

        System.out.println("The msg has been written on all replicas");
    }
}
