package client;

import shared.*;
import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField txtHost, txtPort, txtUsername;
    private JButton btnConnect, btnCancel;
    private boolean connected = false;
    
    private String host;
    private int port;
    private String username;
    
    public LoginDialog(Frame parent) {
        super(parent, "Weather App - Login", true);
        initUI();
    }
    
    private void initUI() {
        setSize(450, 380);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Constants.COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(450, 80));
        
        JLabel lblHeader = IconManager.createIconLabel("cloud_app.png", " Weather Application", 32);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null); // Absolute positioning
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(450, 200));
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        
        // Host label and field
        JLabel lblHost = new JLabel("Server IP:");
        lblHost.setFont(labelFont);
        lblHost.setBounds(50, 30, 100, 25);
        formPanel.add(lblHost);
        
        txtHost = new JTextField(Constants.DEFAULT_HOST);
        txtHost.setFont(fieldFont);
        txtHost.setBounds(180, 30, 220, 35);
        txtHost.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(txtHost);
        
        // Port label and field
        JLabel lblPort = new JLabel("Port:");
        lblPort.setFont(labelFont);
        lblPort.setBounds(50, 80, 100, 25);
        formPanel.add(lblPort);
        
        txtPort = new JTextField(String.valueOf(Constants.DEFAULT_PORT));
        txtPort.setFont(fieldFont);
        txtPort.setBounds(180, 80, 220, 35);
        txtPort.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(txtPort);
        
        // Username label and field
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        lblUsername.setBounds(50, 130, 100, 25);
        formPanel.add(lblUsername);
        
        txtUsername = new JTextField();
        txtUsername.setFont(fieldFont);
        txtUsername.setBounds(180, 130, 220, 35);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(txtUsername);
        
        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        btnPanel.setBackground(Color.WHITE);
        
        btnConnect = IconManager.createIconButton("connect.png", " Connect", 18);
        btnConnect.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConnect.setBackground(Constants.COLOR_SUCCESS);
        btnConnect.setForeground(Color.WHITE);
        btnConnect.setPreferredSize(new Dimension(150, 45));
        btnConnect.setFocusPainted(false);
        btnConnect.setBorderPainted(false);
        btnConnect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCancel = IconManager.createIconButton("disconnect.png", " Cancel", 18);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(Constants.COLOR_DANGER);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(150, 45));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPanel.add(btnConnect);
        btnPanel.add(btnCancel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        
        // Actions
        btnConnect.addActionListener(e -> connect());
        btnCancel.addActionListener(e -> {
            connected = false;
            dispose();
        });
        
        txtUsername.addActionListener(e -> connect());
        
        setLocationRelativeTo(getParent());
    }
    
    private void connect() {
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String u = txtUsername.getText().trim();
        
        if (h.isEmpty() || p.isEmpty() || u.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng điền đầy đủ thông tin!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            this.host = h;
            this.port = Integer.parseInt(p);
            this.username = u;
            this.connected = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Port phải là số nguyên!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConnected() { return connected; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getUsername() { return username; }
}