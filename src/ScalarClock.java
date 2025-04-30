public class ScalarClock {
    private int clock = 0;

    public synchronized void tick() {
        clock++;
    }

    public synchronized void receiveAction(int receivedClock) {
        clock = Math.max(clock, receivedClock) + 1;
    }

    public synchronized int getClock() {
        return clock;
    }
}
