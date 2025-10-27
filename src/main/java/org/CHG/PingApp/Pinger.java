package org.CHG.PingApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Pinger extends JFrame {
    
    // UI Components
    private JTextField hostField;
    private JTextField intervalField;
    private JTextField timeoutField;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton clearButton;
    private JButton saveButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea outputArea;
    
    // Network components
    private SwingWorker<Void, String> pingWorker;
    private boolean isPaused = false;
    private boolean isRunning = false;
    private int successCount = 0;
    private int failureCount = 0;
    
    public Pinger() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Advanced Network IP Pinger v2.0");
        setSize(700, 550);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Input fields
        hostField = new JTextField("8.8.8.8", 15);
        intervalField = new JTextField("1000", 8);
        timeoutField = new JTextField("5000", 8);
        
        // Output area
    outputArea = new JTextArea(); 
    outputArea.setEditable(false); 
    outputArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17)); 
    outputArea.setBackground(Color.BLACK);
    outputArea.setForeground(Color.WHITE); // all text same color

        // Buttons
        startButton = new JButton("Start Ping");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        clearButton = new JButton("Clear Output");
        saveButton = new JButton("Save Log");
        
        // Status components
        statusLabel = new JLabel("Ready to ping...");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        
        // Initial button states
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }
  
          public void playSound() {
    try {
        File soundFile = new File("src/main/resources/bell.wav"); // point to the actual .wav file
        if (!soundFile.exists()) {
            System.err.println("Sound file not found!");
            return;
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Input controls
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        topPanel.setBorder(BorderFactory.createTitledBorder("Ping Configuration"));
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Host input
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Host/IP:"), gbc);
        gbc.gridx = 1;
        topPanel.add(hostField, gbc);
        
        // Interval input
        gbc.gridx = 2;
        topPanel.add(new JLabel("Interval (ms):"), gbc);
        gbc.gridx = 3;
        topPanel.add(intervalField, gbc);
        
        // Timeout input
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Timeout (ms):"), gbc);
        gbc.gridx = 1;
        topPanel.add(timeoutField, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Output area
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ping Results"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel - Controls and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);


        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.CENTER);
        
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        startButton.addActionListener(e -> startPing());
        pauseButton.addActionListener(e -> togglePause());
        stopButton.addActionListener(e -> stopPing());
        clearButton.addActionListener(e -> clearOutput());
        saveButton.addActionListener(e -> saveLog());
    }
 private void saveLog() {
    try {
        // Create the LogFiles folder inside src if it doesn't exist
        java.nio.file.Path logDir = java.nio.file.Paths.get("src", "LogFiles");
        if (!java.nio.file.Files.exists(logDir)) {
            java.nio.file.Files.createDirectories(logDir);
        }

        // Create a log file with timestamp
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        String fileName = "PingLogs_" + timestamp + ".txt";
        java.nio.file.Path logFile = logDir.resolve(fileName);

        // Write the contents of outputArea to the file
        java.nio.file.Files.write(logFile, outputArea.getText().getBytes());

        JOptionPane.showMessageDialog(this, "Log saved to:\n" + logFile.toAbsolutePath(),
                                      "Log Saved", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error saving log: " + ex.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    private void startPing() {
        String host = hostField.getText().trim();
        if (host.isEmpty()) {
            showError("Please enter a host/IP address");
            return;
        }
        
        int interval, timeout;
        try {
            interval = Integer.parseInt(intervalField.getText().trim());
            timeout = Integer.parseInt(timeoutField.getText().trim());
            
            if (interval < 100) interval = 100; // Minimum 100ms
            if (timeout < 1000) timeout = 1000; // Minimum 1s
            
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers for interval and timeout");
            return;
        }
        
        // Reset counters
        successCount = 0;
        failureCount = 0;
        isPaused = false;
        isRunning = true;
        
        // Update UI state
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        progressBar.setVisible(true);
        statusLabel.setText("Starting ping to " + host + "...");
        
        // Start ping worker
        pingWorker = new PingWorker(host, interval, timeout);
        pingWorker.execute();
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume" : "Pause");
        statusLabel.setText(isPaused ? "Ping paused" : "Ping resumed");
    }
    
    private void stopPing() {
        isRunning = false;
        if (pingWorker != null && !pingWorker.isDone()) {
            pingWorker.cancel(true);
        }
        
        // Update UI state
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        stopButton.setEnabled(false);
        progressBar.setVisible(false);
        
        appendOutput("\n=== Ping stopped ===", Color.GREEN);
        appendOutput("Total packets sent: " + (successCount + failureCount), Color.GREEN);
        appendOutput("Successful: " + successCount + " (" + 
                    (successCount + failureCount > 0 ? 
                     String.format("%.1f%%", (successCount * 100.0) / (successCount + failureCount)) : "0%") + ")", Color.GREEN);
        appendOutput("Failed: " + failureCount + "\n", Color.RED);
        
        statusLabel.setText("Ping stopped. Ready for next ping.");
    }
    
    private void clearOutput() {
        outputArea.setText("");
        successCount = 0;
        failureCount = 0;
    }
    
    private void appendOutput(String text, Color GREEN) {
        SwingUtilities.invokeLater(() -> {
        String pingResult = "Request timed out"; // example result
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String runSystemPing(String host, int timeout) {
    try {
        ProcessBuilder pb;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows
            pb = new ProcessBuilder("ping", "-n", "1", "-w", String.valueOf(timeout), host);
        } else {
            // Linux / Mac
            pb = new ProcessBuilder("ping", "-c", "1", "-W", String.valueOf(timeout / 1000), host);
        }

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String lower = line.toLowerCase();
            // Only return the actual reply line (ignore headers/stats)
            if (lower.contains("ttl") || lower.startsWith("reply from") || lower.contains("bytes from")) {
                return line.trim();
            }
        }
        process.waitFor();
        return "Request timed out";
    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }
}


    // Ping Worker Class
    private class PingWorker extends SwingWorker<Void, String> {
        private final String host;
        private final int interval;
        private final int timeout;
        
        public PingWorker(String host, int interval, int timeout) {
            this.host = host;
            this.interval = interval;
            this.timeout = timeout;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");            
            int sequenceNumber = 1;

            publish("=== Starting ping to " + host + " ===");
            publish("Ping interval: " + interval + "ms, Timeout: " + timeout + "ms\n");

            try {
                // Validate host first
                InetAddress.getByName(host);
                
while (isRunning && !isCancelled()) {
    if (!isPaused) {
        String timestamp = dateTimeFormat.format(new Date());

        String replyLine = runSystemPing(host, timeout);

        // Determine if the ping was successful
        boolean successful = !(replyLine.toLowerCase().contains("timed out") || 
                               replyLine.toLowerCase().contains("unreachable"));

        // Play sound if ping failed
        if (!successful) {
            playSound();
            failureCount++;
        } else {
            successCount++;
        }

        // Publish output with timestamp
        publish(String.format("[%s] #%d: %s", timestamp, sequenceNumber, replyLine));
        sequenceNumber++;

        // Update status on UI
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(String.format("Pinging %s... Success: %d, Failed: %d",
                    host, successCount, failureCount));
        });
    }

    // Wait for next ping
    Thread.sleep(interval);
}

            } catch (UnknownHostException ex) {
                publish("Error: Unknown host '" + host + "'");
            } catch (InterruptedException ex) {
                publish("Ping interrupted");
            }
            
            return null;
        }
        
        @Override
        protected void process(List<String> chunks) {
            for (String line : chunks) {
                appendOutput(line, Color.GREEN);
            }
        }
        
        @Override
        protected void done() {
            if (!isCancelled()) {
                stopPing();
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new Pinger().setVisible(true);
        });
    }
}