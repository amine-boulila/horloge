import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class Interface extends JFrame {
    private Map<Integer, JTextArea> clockAreas = new HashMap<>();
    private final Object stepLock = new Object();
    private boolean nextStepReady = false;
    private int completedProcesses = 0;
    private static final int TOTAL_PROCESSES = 4;

    public synchronized void notifyProcessFinished(int processId) {
        completedProcesses++;
        System.out.println("✅ Process " + processId + " finished. [" + completedProcesses + "/" + TOTAL_PROCESSES + "]");

        if (completedProcesses == TOTAL_PROCESSES) {
            System.out.println("🎉 All processes complete. Exiting simulation...");
            dispose(); // close GUI
            System.exit(0); // exit entire program
        }
    }


    public Interface() {
        setTitle("Distributed Clocks Interface");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(2, 2));
        for (int i = 0; i < 4; i++) {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder("Process " + i));
            panel.setLayout(new BorderLayout());

            JTextArea clockArea = new JTextArea(6, 20);
            clockArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            clockArea.setEditable(false);
            clockArea.setLineWrap(true);
            clockArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(clockArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            clockAreas.put(i, clockArea);
            gridPanel.add(panel);
        }

        JButton nextButton = new JButton("Next Step ▶");
        nextButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextButton.addActionListener((ActionEvent e) -> {
            synchronized (stepLock) {
                nextStepReady = true;
                stepLock.notifyAll();
            }
        });

        add(gridPanel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void updateClock(int processId, String clockText) {
        JTextArea area = clockAreas.get(processId);
        if (area != null) {
            area.setText("Clock:\n" + clockText);
        }
    }

    // This method blocks the calling process until user presses "Next"
    public void waitForNextStep() {
        synchronized (stepLock) {
            while (!nextStepReady) {
                try {
                    stepLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nextStepReady = false;
        }
    }
}
