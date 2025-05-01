public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java Main <1=scalar | 2=vector | 3=matrix>");
            return;
        }

        String clockType;
        switch (args[0]) {
            case "1":
                clockType = "scalar";
                break;
            case "2":
                clockType = "vector";
                break;
            case "3":
                clockType = "matrix";
                break;
            default:
                System.out.println("Invalid argument. Use 1 for scalar, 2 for vector, 3 for matrix.");
                return;
        }

        int[] ports = {5000, 5001, 5002, 5003};
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
