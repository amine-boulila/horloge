import java.util.Arrays;

public class VectorClock {
    private int[] clock;

    public VectorClock(int numProcesses) {
        clock = new int[numProcesses];
    }

    public synchronized void tick(int processId) {
        clock[processId]++;
    }

    public synchronized void receiveAction(int processId,int[] receivedClock) {
        for (int i = 0; i < clock.length; i++) {
            clock[i] = Math.max(clock[i], receivedClock[i]);
        }
        clock[processId]++;
        
    }

    public synchronized int[] getClock() {
        return Arrays.copyOf(clock, clock.length);
    }
}
