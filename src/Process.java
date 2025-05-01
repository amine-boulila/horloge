import java.io.*;
import java.net.*;
import java.util.Random;

public class Process extends Thread {
    private int id;
    private int port;
    private int[] otherPorts;
    private String clockType;

    private ScalarClock scalarClock;
    private VectorClock vectorClock;
    private MatrixClock matrixClock;
    
    private int receiveCount = 0;
    private static final int MAX_RECEIVES = 3;

    private int sendCount = 0;
    private static final int MAX_SENDS = 4; 


    private ServerSocket serverSocket;
    private Random random = new Random();
    private static Interface visualizer;

public static void setVisualizer(Interface v) {
    visualizer = v;
}

private void updateVisualizer() {
    if (visualizer != null) {
        if (clockType.equals("scalar")) {
            visualizer.updateClock(id, Integer.toString(scalarClock.getClock()));
        } else if (clockType.equals("vector")) {
            visualizer.updateClock(id, arrayToString(vectorClock.getClock()));
        } else if (clockType.equals("matrix")) {
            visualizer.updateClock(id, matrixToString(matrixClock.getClock()));
        }
    }
}

private String arrayToString(int[] arr) {
    StringBuilder sb = new StringBuilder("[");
    for (int val : arr) sb.append(val).append(" ");
    sb.append("]");
    return sb.toString();
}

private String matrixToString(int[][] matrix) {
    StringBuilder sb = new StringBuilder();
    for (int[] row : matrix) {
        sb.append("[");
        for (int val : row) sb.append(val).append(" ");
        sb.append("]");
    }
    return sb.toString();
}


    public Process(int id, int port, int[] otherPorts, String clockType) throws IOException {
        this.id = id;
        this.port = port;
        this.otherPorts = otherPorts;
        this.clockType = clockType;

        serverSocket = new ServerSocket(port);

        if (clockType.equals("scalar")) scalarClock = new ScalarClock();
        else if (clockType.equals("vector")) vectorClock = new VectorClock(4);
        else if (clockType.equals("matrix")) matrixClock = new MatrixClock(4, id);
    }

    public void run() {
        new Thread(this::listen).start();

        try {
            for (int i = 0; i < 5; i++) { // 5 local instructions
                localEvent();
                Thread.sleep(1000);
            }
            sendMessage();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void localEvent() {
        if (clockType.equals("scalar")) {
            scalarClock.tick();
            System.out.println("Process " + id + " Scalar Clock after local event: " + scalarClock.getClock());
        } else if (clockType.equals("vector")) {
            vectorClock.tick(id);
            System.out.print("Process " + id + " Vector Clock after local event: ");
            printArray(vectorClock.getClock());
        } else if (clockType.equals("matrix")) {
            matrixClock.tick();
            System.out.println("Process " + id + " Matrix Clock after local event:");
            printMatrix(matrixClock.getClock());
        }
        updateVisualizer();
        if (visualizer != null) visualizer.waitForNextStep();
    }

    private void sendMessage() {
    for (int targetPort : otherPorts) {
        try (Socket socket = new Socket("localhost", targetPort)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Object clockValue = null;
            int targetProcessId =targetPort-5000;

            if (clockType.equals("scalar")) {
                scalarClock.tick();
                clockValue = scalarClock.getClock();
            } else if (clockType.equals("vector")) {
                vectorClock.tick(id);
                clockValue = vectorClock.getClock();
            } else if (clockType.equals("matrix")) {
                matrixClock.tick();
                matrixClock.othertic(targetProcessId);
                clockValue = matrixClock.getClock();
            }

            out.writeObject(new Message(id, clockValue));
            sendCount++;

            System.out.println("Process " + id + " sent message to Process " + targetProcessId);

            updateVisualizer();
            if (visualizer != null) visualizer.waitForNextStep();

        } catch (IOException e) {
             e.printStackTrace();
        }
    }
}



    private void listen() {
        try {
            while (receiveCount < MAX_RECEIVES) {
                Socket socket = serverSocket.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) in.readObject();
                receiveCount++;

                // 🔵 Log receive info
                System.out.println("Process " + id + " received message from Process " + message.senderId);

                // Update clocks...
                if (clockType.equals("scalar")) {
                    scalarClock.receiveAction((Integer) message.clock);
                } else if (clockType.equals("vector")) {
                    vectorClock.receiveAction(id,(int[]) message.clock);
                } else if (clockType.equals("matrix")) {
                    matrixClock.receiveAction(message.senderId, (int[][]) message.clock);
                }

                updateVisualizer();
                if (visualizer != null) visualizer.waitForNextStep();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void printArray(int[] arr) {
        System.out.print("[");
        for (int val : arr) System.out.print(val + " ");
        System.out.println("]");
    }

    private void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) System.out.print(val + " ");
            System.out.println();
        }
    }
}
