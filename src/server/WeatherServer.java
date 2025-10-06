package server;

import shared.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class WeatherServer extends JFrame {
    private JTextField txtPort;
    private JButton btnStart, btnStop, btnRefresh, btnClear;
    private JTable tblClients;
    private DefaultTableModel tableModel;
    private JTextArea txtLog;
    private JLabel lblStatus;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Map<String, ClientHandler> clients;
    private boolean serverRunning = false;
    
    public WeatherServer() {
        clients = new ConcurrentHashMap<>();
        executorService = Executors.newCachedThreadPool();
        initUI();
    }
    
    private void initUI() {
        setTitle("☁ Weather Server - Quản lý kết nối");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Constants.COLOR_LIGHT);
        
        // Top Panel - Server Control
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Constants.COLOR_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblPort = new JLabel("Port:");
        lblPort.setForeground(Color.WHITE);
        lblPort.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtPort = new JTextField(String.valueOf(Constants.DEFAULT_PORT), 8);
        txtPort.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        btnStart = createButton("▶ Start Server", Constants.COLOR_SUCCESS);
        btnStop = createButton("⏹ Stop Server", Constants.COLOR_DANGER);
        btnRefresh = createButton("🔄 Refresh", Constants.COLOR_PRIMARY);
        btnClear = createButton("🗑 Clear Log", new Color(149, 165, 166));
        
        btnStop.setEnabled(false);
        
        lblStatus = new JLabel("● Offline");
        lblStatus.setForeground(Constants.COLOR_DANGER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        topPanel.add(lblPort);
        topPanel.add(txtPort);
        topPanel.add(btnStart);
        topPanel.add(btnStop);
        topPanel.add(btnRefresh);
        topPanel.add(btnClear);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(lblStatus);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        
        // Client table
        JPanel clientPanel = new JPanel(new BorderLayout(5, 5));
        clientPanel.setBackground(Color.WHITE);
        clientPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            "👥 Danh sách Client đang kết nối",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), Constants.COLOR_DARK));
        
        String[] columns = {"STT", "Username", "IP Address", "Port", "Thời gian kết nối", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column editable
            }
        };
        
        tblClients = new JTable(tableModel);
        tblClients.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblClients.setRowHeight(30);
        tblClients.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblClients.getTableHeader().setBackground(Constants.COLOR_PRIMARY);
        tblClients.getTableHeader().setForeground(Color.WHITE);
        
        // Add button to action column
        tblClients.getColumn("Hành động").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("❌ Disconnect");
            btn.setBackground(Constants.COLOR_DANGER);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            return btn;
        });
        
        tblClients.getColumn("Hành động").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton btn = new JButton("❌ Disconnect");
                btn.setBackground(Constants.COLOR_DANGER);
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    disconnectClient(row);
                    fireEditingStopped();
                });
                return btn;
            }
        });
        
        JScrollPane scrollClients = new JScrollPane(tblClients);
        clientPanel.add(scrollClients, BorderLayout.CENTER);
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            "📝 Server Log",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), Constants.COLOR_DARK));
        
        txtLog = new JTextArea();
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(40, 44, 52));
        txtLog.setForeground(new Color(171, 178, 191));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        logPanel.add(scrollLog, BorderLayout.CENTER);
        
        splitPane.setTopComponent(clientPanel);
        splitPane.setBottomComponent(logPanel);
        add(splitPane, BorderLayout.CENTER);
        
        // Button actions
        btnStart.addActionListener(e -> startServer());
        btnStop.addActionListener(e -> stopServer());
        btnRefresh.addActionListener(e -> refreshClientList());
        btnClear.addActionListener(e -> txtLog.setText(""));
        
        setLocationRelativeTo(null);
        log("Server initialized. Ready to start.");
    }
    
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void startServer() {
        try {
            int port = Integer.parseInt(txtPort.getText());
            serverSocket = new ServerSocket(port);
            serverRunning = true;
            
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            txtPort.setEnabled(false);
            lblStatus.setText("● Online");
            lblStatus.setForeground(Constants.COLOR_SUCCESS);
            
            log("✓ Server started on port " + port);
            
            // Accept clients in background
            executorService.submit(() -> {
                while (serverRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(clientSocket, this);
                        String address = handler.getClientAddress();
                        clients.put(address, handler);
                        executorService.submit(handler);
                        
                        log("✓ New client connected: " + address);
                        refreshClientList();
                    } catch (IOException e) {
                        if (serverRunning) {
                            log("✗ Error accepting client: " + e.getMessage());
                        }
                    }
                }
            });
            
        } catch (Exception e) {
            log("✗ Failed to start server: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Server Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopServer() {
        try {
            serverRunning = false;
            
            // Disconnect all clients
            for (ClientHandler client : clients.values()) {
                client.disconnect();
            }
            clients.clear();
            
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            txtPort.setEnabled(true);
            lblStatus.setText("● Offline");
            lblStatus.setForeground(Constants.COLOR_DANGER);
            
            refreshClientList();
            log("✓ Server stopped");
            
        } catch (IOException e) {
            log("✗ Error stopping server: " + e.getMessage());
        }
    }
    
    private void disconnectClient(int row) {
        String address = (String) tableModel.getValueAt(row, 2) + ":" + tableModel.getValueAt(row, 3);
        ClientHandler client = clients.get(address);
        if (client != null) {
            client.disconnect();
            log("✓ Disconnected client: " + address);
        }
    }
    
    public void removeClient(String address) {
        clients.remove(address);
        SwingUtilities.invokeLater(this::refreshClientList);
    }
    
    private void refreshClientList() {
        tableModel.setRowCount(0);
        int i = 1;
        for (ClientHandler client : clients.values()) {
            String[] parts = client.getClientAddress().split(":");
            tableModel.addRow(new Object[]{
                i++,
                client.getUsername() != null ? client.getUsername() : "N/A",
                parts[0],
                parts[1],
                client.getConnectedTime(),
                "Disconnect"
            });
        }
    }
    
    public void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + timestamp + "] " + message + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WeatherServer().setVisible(true);
        });
    }
}