import java.io.Serializable;

public class Message implements Serializable {
    public int senderId;
    public Object clock; // Can be Integer, int[], or int[][] depending on clock type

    public Message(int senderId, Object clock) {
        this.senderId = senderId;
        this.clock = clock;
    }
}
