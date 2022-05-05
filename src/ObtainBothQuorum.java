import java.util.ArrayList;

public class ObtainBothQuorum  {
    String msg;
    String[] servers;
    long lamportsClock;
    Quorum quorum;

    public ObtainBothQuorum(String msg, String[] servers, long lamportsClock, Quorum quorum) {
        this.msg = msg;
        this.servers = servers;
        this.lamportsClock = lamportsClock;
        this.quorum=quorum;
    }

    public void obtain() {
        int idx1 = 0, idx2 = 1;
        //Create two threads one for each server and infrom it that it has a msg to write, acquire the lock
        ObtainQuorum quorum1 = new ObtainQuorum(msg, servers[idx1], lamportsClock, quorum, idx1,idx2);
        Thread client1 = new Thread(quorum1);

        ObtainQuorum quorum2 = new ObtainQuorum(msg, servers[idx2], lamportsClock, quorum, idx2,idx1);
        Thread client2 = new Thread(quorum2);
        try {
            //Start the process as threads and wait for the them to finish
            client1.start();
            client2.start();
            client1.join();
            client2.join();
            System.out.println("Obtained consent from both in the quorum");
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
