package client;

import server.WeatherData;
import shared.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class WeatherClient extends JFrame {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private WeatherPanel weatherPanel;
    private JButton btnRefresh, btnDisconnect;
    private JLabel lblStatus, lblUsername;
    private String username;
    private boolean connected = false;
    private Timer autoRefreshTimer;
    
    public WeatherClient() {
        initUI();
        showLoginDialog();
    }
    
    private void initUI() {
        setTitle("☁ Weather Client");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        
        // Top panel - Control bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Constants.COLOR_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Left side - User info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        lblUsername = new JLabel("👤 Guest");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(Color.WHITE);
        
        lblStatus = new JLabel("● Disconnected");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Constants.COLOR_DANGER);
        
        leftPanel.add(lblUsername);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(lblStatus);
        
        // Right side - Control buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        btnRefresh = new JButton("🔄 Refresh Weather");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(Constants.COLOR_PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setEnabled(false);
        
        btnDisconnect = new JButton("❌ Disconnect");
        btnDisconnect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDisconnect.setBackground(Constants.COLOR_DANGER);
        btnDisconnect.setForeground(Color.WHITE);
        btnDisconnect.setFocusPainted(false);
        btnDisconnect.setBorderPainted(false);
        btnDisconnect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDisconnect.setEnabled(false);
        
        rightPanel.add(btnRefresh);
        rightPanel.add(btnDisconnect);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        // Center - Weather panel
        weatherPanel = new WeatherPanel();
        
        add(topPanel, BorderLayout.NORTH);
        add(weatherPanel, BorderLayout.CENTER);
        
        // Button actions
        btnRefresh.addActionListener(e -> requestWeather());
        btnDisconnect.addActionListener(e -> disconnect());
        
        // Auto refresh every 30 seconds
        autoRefreshTimer = new Timer(30000, e -> {
            if (connected) {
                requestWeather();
            }
        });
        
        setLocationRelativeTo(null);
    }
    
    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        
        if (loginDialog.isConnected()) {
            connectToServer(
                loginDialog.getHost(),
                loginDialog.getPort(),
                loginDialog.getUsername()
            );
        } else {
            System.exit(0);
        }
    }
    
    private void connectToServer(String host, int port, String user) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            this.username = user;
            this.connected = true;
            
            // Send login message
            Message loginMsg = new Message(Constants.MSG_LOGIN, username);
            out.writeObject(loginMsg);
            out.flush();
            
            // Wait for response
            Message response = (Message) in.readObject();
            
            if (Constants.MSG_SUCCESS.equals(response.getType())) {
                lblUsername.setText("👤 " + username);
                lblStatus.setText("● Connected to " + host + ":" + port);
                lblStatus.setForeground(Constants.COLOR_SUCCESS);
                btnRefresh.setEnabled(true);
                btnDisconnect.setEnabled(true);
                
                // Start listening for messages
                startListening();
                
                // Auto refresh weather on connect
                requestWeather();
                
                // Start auto-refresh timer
                autoRefreshTimer.start();
                
                JOptionPane.showMessageDialog(this,
                    "Connected successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new Exception("Login failed");
            }
            
        } catch (Exception e) {
            connected = false;
            JOptionPane.showMessageDialog(this,
                "Cannot connect to server:\n" + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void startListening() {
        new Thread(() -> {
            try {
                while (connected) {
                    Message message = (Message) in.readObject();
                    handleMessage(message);
                }
            } catch (Exception e) {
                if (connected) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "Connection lost!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        disconnect();
                    });
                }
            }
        }).start();
    }
    
    private void handleMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            if (Constants.MSG_WEATHER_RESPONSE.equals(message.getType())) {
                WeatherData data = (WeatherData) message.getData();
                weatherPanel.updateWeather(data);
            }
        });
    }
    
    private void requestWeather() {
        if (!connected) return;
        
        try {
            Message request = new Message(Constants.MSG_WEATHER_REQUEST, username);
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            weatherPanel.showError("Failed to request weather data");
            e.printStackTrace();
        }
    }
    
    private void disconnect() {
        try {
            connected = false;
            autoRefreshTimer.stop();
            
            if (out != null) {
                Message logoutMsg = new Message(Constants.MSG_LOGOUT, username);
                out.writeObject(logoutMsg);
                out.flush();
            }
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            
            lblStatus.setText("● Disconnected");
            lblStatus.setForeground(Constants.COLOR_DANGER);
            btnRefresh.setEnabled(false);
            btnDisconnect.setEnabled(false);
            
            weatherPanel.showError("Disconnected from server");
            
            int choice = JOptionPane.showConfirmDialog(this,
                "Disconnected. Do you want to reconnect?",
                "Disconnected",
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                showLoginDialog();
            } else {
                System.exit(0);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WeatherClient().setVisible(true);
        });
    }
}