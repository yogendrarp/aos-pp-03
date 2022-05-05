
public class ObtainBothQuorum {
    String msg;
    String[] servers;
    long lamportsClock;
    Quorum quorum;

    public ObtainBothQuorum(String msg, String[] servers, long lamportsClock, Quorum quorum) {
        this.msg = msg;
        this.servers = servers;
        this.lamportsClock = lamportsClock;
        this.quorum = quorum;
    }

    public void obtain() {

        if (quorum.vote1 && quorum.vote2) {
            System.out.println("Already have votes from the past, proceeding!");
            return;
        }
        int idx1 = 0, idx2 = 1;
        Thread client1 = null, client2 = null;
        //Create two threads one for each server and infrom it that it has a msg to write, acquire the lock
        if (!quorum.vote1) {
            ObtainQuorum quorum1 = new ObtainQuorum(msg, servers[idx1], lamportsClock, quorum,idx1);
            client1 = new Thread(quorum1);
            client1.start();
        }
        if (!quorum.vote2) {
            ObtainQuorum quorum2 = new ObtainQuorum(msg, servers[idx2], lamportsClock, quorum,idx2);
            client2 = new Thread(quorum2);
            client2.start();
        }
        try {
            //Start the process as threads and wait for the them to finish
            if (!quorum.vote1)
                client1.join();
            if (!quorum.vote2)
                client2.join();
            System.out.println("Obtained consent from both in the quorum");
        } catch (InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
