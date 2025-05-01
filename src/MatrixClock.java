public class MatrixClock {
    private int[][] clock;
    private int id;

    public MatrixClock(int numProcesses, int id) {
        this.id = id;
        clock = new int[numProcesses][numProcesses];
    }

    public synchronized void tick() {
        clock[id][id]++;
    }
     public synchronized void othertic(int processId) {
        clock[id][processId]++;
    }

    public synchronized void receiveAction(int senderId, int[][] receivedClock) {
        for (int i = 0; i < clock.length; i++) {
            for (int j = 0; j < clock.length; j++) {
                clock[i][j] = Math.max(clock[i][j], receivedClock[i][j]);
            }
        }
        clock[id][id]++;
    }

    public synchronized int[][] getClock() {
        int[][] copy = new int[clock.length][clock.length];
        for (int i = 0; i < clock.length; i++) {
            System.arraycopy(clock[i], 0, copy[i], 0, clock.length);
        }
        return copy;
    }
}
