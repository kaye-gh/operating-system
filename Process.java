import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Process {
    int pid; // Process ID
    int burstTime; // Burst Time
    int priority; // Priority (higher value = higher priority)
    int arrivalTime; // Arrival Time
    int waitingTime = 0; // Waiting Time
    int turnaroundTime = 0; // Turnaround Time
    int completionTime = 0; // Completion Time

    Process(int pid, int burstTime, int priority, int arrivalTime) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
    }
}

public class PrioritySchedulingGUI extends JFrame {
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JTextField txtPID, txtBurstTime, txtPriority, txtArrivalTime;
    private JButton btnAddProcess, btnCalculate;
    private ArrayList<Process> processes = new ArrayList<>();
    private JTextArea ganttChartArea;

    public PrioritySchedulingGUI() {
        setTitle("Priority-Based CPU Scheduling");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table for process details
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Process ID", "Burst Time", "Priority", "Arrival Time", "Waiting Time", "Turnaround Time", "Completion Time"});
        processTable = new JTable(tableModel);
        add(new JScrollPane(processTable), BorderLayout.CENTER);

        // Panel for input and buttons
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtPID = new JTextField();
        txtBurstTime = new JTextField();
        txtPriority = new JTextField();
        txtArrivalTime = new JTextField();
        btnAddProcess = new JButton("Add Process");
        btnCalculate = new JButton("Calculate Schedule");

        inputPanel.add(new JLabel("Process ID:"));
        inputPanel.add(txtPID);
        inputPanel.add(new JLabel("Burst Time:"));
        inputPanel.add(txtBurstTime);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(txtPriority);
        inputPanel.add(new JLabel("Arrival Time:"));
        inputPanel.add(txtArrivalTime);
        inputPanel.add(new JLabel());
        inputPanel.add(btnAddProcess);
        inputPanel.add(new JLabel());
        inputPanel.add(btnCalculate);

        add(inputPanel, BorderLayout.NORTH);

        // Text Area for Gantt Chart
        ganttChartArea = new JTextArea(5, 50);
        ganttChartArea.setEditable(false);
        ganttChartArea.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        add(new JScrollPane(ganttChartArea), BorderLayout.SOUTH);

        // Button Listeners
        btnAddProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProcess();
            }
        });

        btnCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateSchedule();
            }
        });

        setVisible(true);
    }

    private void addProcess() {
        try {
            int pid = Integer.parseInt(txtPID.getText().trim());
            int burstTime = Integer.parseInt(txtBurstTime.getText().trim());
            int priority = Integer.parseInt(txtPriority.getText().trim());
            int arrivalTime = Integer.parseInt(txtArrivalTime.getText().trim());
            processes.add(new Process(pid, burstTime, priority, arrivalTime));
            tableModel.addRow(new Object[]{pid, burstTime, priority, arrivalTime, "", "", ""});
            txtPID.setText("");
            txtBurstTime.setText("");
            txtPriority.setText("");
            txtArrivalTime.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateSchedule() {
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes to schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sort processes by arrival time first, then by priority (higher priority number first)
        Collections.sort(processes, (p1, p2) -> {
            if (p1.arrivalTime == p2.arrivalTime) {
                return Integer.compare(p2.priority, p1.priority); // Higher priority first
            }
            return Integer.compare(p1.arrivalTime, p2.arrivalTime);
        });

        int currentTime = 0;
        ganttChartArea.setText("Gantt Chart:\n");

        // Clear previous data in table
        tableModel.setRowCount(0);

        for (Process p : processes) {
            if (currentTime < p.arrivalTime) { // Idle time simulation
                ganttChartArea.append("| Idle : " + currentTime + " - " + p.arrivalTime + " |\n");
                currentTime = p.arrivalTime;
            }
            p.waitingTime = currentTime - p.arrivalTime;
            p.turnaroundTime = p.waitingTime + p.burstTime;
            p.completionTime = currentTime + p.burstTime;

            ganttChartArea.append("| P" + p.pid + " : " + currentTime + " - " + p.completionTime + " |\n");
            currentTime += p.burstTime;

            tableModel.addRow(new Object[]{p.pid, p.burstTime, p.priority, p.arrivalTime, p.waitingTime, p.turnaroundTime, p.completionTime});
        }
        ganttChartArea.append("\n");
        JOptionPane.showMessageDialog(this, "Scheduling calculation completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrioritySchedulingGUI::new);
    }
}
