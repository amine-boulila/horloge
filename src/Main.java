public class Main {
    public static void main(String[] args) throws Exception {
        int[] ports = {5000, 5001, 5002, 5003};
        String clockType = "matrix"; // Change to "vector" or "matrix" as needed
        Interface visualizer = new Interface();
        Process.setVisualizer(visualizer);


        for (int i = 0; i < 4; i++) {
            int[] others = new int[3];
            int idx = 0;
            for (int j = 0; j < 4; j++) {
                if (i != j) others[idx++] = ports[j];
            }
            new Process(i, ports[i], others, clockType).start();
        }
    }
}
