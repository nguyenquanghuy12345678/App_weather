package client;

import shared.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommunityReportsPanel extends JPanel {
    private JTable tblReports;
    private DefaultTableModel tableModel;
    private JTextArea txtComment;
    private JComboBox<String> cboAccuracy;
    private JLabel lblStats, lblAvgAccuracy;
    private CommunityReportsManager reportsManager;
    private String currentLocation;
    private String username; // Logged-in username
    private boolean showAllLocations = true; // Default: show all
    private JButton btnFilter; // Reference to filter button
    
    public CommunityReportsPanel() {
        reportsManager = new CommunityReportsManager();
        this.username = "Guest"; // Default
        initUI();
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setServerCallback(CommunityReportsManager.ServerCallback callback) {
        reportsManager.setServerCallback(callback);
    }
    
    public CommunityReportsManager getReportsManager() {
        return reportsManager;
    }
    
    public void refreshReportsDisplay() {
        SwingUtilities.invokeLater(() -> {
            updateReportsTable();
        });
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Community Weather Reports");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Constants.COLOR_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        lblStats = new JLabel("Total Reports: 0");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStats.setForeground(Color.GRAY);
        
        lblAvgAccuracy = new JLabel("Avg: -");
        lblAvgAccuracy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAvgAccuracy.setForeground(new Color(46, 125, 50));
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(lblStats);
        statsPanel.add(new JLabel("|"));
        statsPanel.add(lblAvgAccuracy);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        
        // Top - Submit report panel
        JPanel submitPanel = new JPanel(new BorderLayout(10, 10));
        submitPanel.setBackground(new Color(245, 247, 250));
        submitPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            "📝 Submit Your Weather Report",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), Constants.COLOR_DARK));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Accuracy rating
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblAccuracy = new JLabel("Forecast Accuracy:");
        lblAccuracy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblAccuracy, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] accuracyOptions = {"⭐ Very Poor", "⭐⭐ Poor", "⭐⭐⭐ Good", "⭐⭐⭐⭐ Very Good", "⭐⭐⭐⭐⭐ Excellent"};
        cboAccuracy = new JComboBox<>(accuracyOptions);
        cboAccuracy.setSelectedIndex(2); // Default to "Good"
        cboAccuracy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(cboAccuracy, gbc);
        
        // Comment
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel lblComment = new JLabel("Your Comment (optional):");
        lblComment.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblComment, gbc);
        
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        txtComment = new JTextArea(4, 40);
        txtComment.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        txtComment.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JScrollPane scrollComment = new JScrollPane(txtComment);
        formPanel.add(scrollComment, gbc);
        
        // Submit button
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnSubmit = new JButton("📤 Submit Report");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(Constants.COLOR_SUCCESS);
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorderPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(150, 35));
        btnSubmit.addActionListener(e -> submitReport());
        buttonPanel.add(btnSubmit);
        
        // Toggle filter button - changes between Show All and Filter modes
        btnFilter = new JButton("📍 Filter Location");
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFilter.setBackground(new Color(108, 117, 125)); // Gray when showing all
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.setBorderPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.setPreferredSize(new Dimension(170, 35));
        btnFilter.addActionListener(e -> toggleFilter());
        buttonPanel.add(btnFilter);
        
        formPanel.add(buttonPanel, gbc);
        
        submitPanel.add(formPanel, BorderLayout.CENTER);
        
        // Bottom - Reports table
        JPanel reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBackground(Color.WHITE);
        reportsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            "Recent Community Reports",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), Constants.COLOR_DARK));
        
        String[] columns = {"Location", "Rating", "Comment", "User", "Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblReports = new JTable(tableModel);
        tblReports.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblReports.setRowHeight(30);
        tblReports.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblReports.getTableHeader().setBackground(Constants.COLOR_PRIMARY);
        tblReports.getTableHeader().setForeground(Color.WHITE);
        tblReports.setSelectionBackground(new Color(206, 224, 255));
        tblReports.setGridColor(new Color(222, 226, 230));
        
        // Set column widths
        tblReports.getColumnModel().getColumn(0).setPreferredWidth(150); // Location
        tblReports.getColumnModel().getColumn(1).setPreferredWidth(100); // Rating
        tblReports.getColumnModel().getColumn(2).setPreferredWidth(300); // Comment
        tblReports.getColumnModel().getColumn(3).setPreferredWidth(100); // User
        tblReports.getColumnModel().getColumn(4).setPreferredWidth(120); // Time
        
        JScrollPane scrollTable = new JScrollPane(tblReports);
        reportsPanel.add(scrollTable, BorderLayout.CENTER);
        
        splitPane.setTopComponent(submitPanel);
        splitPane.setBottomComponent(reportsPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        updateReportsTable();
    }
    
    public void setLocation(String location) {
        this.currentLocation = location;
        updateReportsTable();
    }
    
    private void toggleFilter() {
        showAllLocations = !showAllLocations;
        
        if (showAllLocations) {
            // Now showing all locations
            btnFilter.setText("📍 Filter Location");
            btnFilter.setBackground(new Color(108, 117, 125)); // Gray
        } else {
            // Now filtering by current location
            btnFilter.setText("🌍 Show All");
            btnFilter.setBackground(Constants.COLOR_PRIMARY); // Blue
        }
        
        updateReportsTable();
    }
    
    private void submitReport() {
        if (currentLocation == null || currentLocation.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a location first!",
                "No Location",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int accuracy = cboAccuracy.getSelectedIndex() + 1; // 1-5 stars
        String comment = txtComment.getText().trim();
        
        reportsManager.addReport(
            currentLocation,
            accuracy,
            comment.isEmpty() ? "No comment" : comment,
            this.username
        );
        
        updateReportsTable();
        
        // Clear form
        txtComment.setText("");
        cboAccuracy.setSelectedIndex(2);
        
        JOptionPane.showMessageDialog(this,
            "Thank you for your feedback!",
            "Report Submitted",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateReportsTable() {
        tableModel.setRowCount(0);
        
        // Get reports from database
        List<WeatherReport> allReports = reportsManager.getAllReports();
        List<WeatherReport> filteredReports = allReports;
        
        // Filter by current location if NOT showing all AND location is set
        if (!showAllLocations && currentLocation != null && !currentLocation.isEmpty()) {
            filteredReports = reportsManager.getReports(currentLocation);
        }
        
        for (WeatherReport report : filteredReports) {
            tableModel.addRow(new Object[]{
                report.location,
                "⭐".repeat(report.accuracy),
                report.comment,
                report.username,
                report.timestamp.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
            });
        }
        
        // Update statistics
        lblStats.setText(String.format("Total Reports: %d (Showing: %d)", 
            allReports.size(), filteredReports.size()));
        
        // Update average accuracy for current location
        if (currentLocation != null && !currentLocation.isEmpty()) {
            ReportStats stats = reportsManager.getStatsForLocation(currentLocation);
            if (stats.totalReports > 0) {
                lblAvgAccuracy.setText(String.format("Avg: %.1f⭐", stats.averageAccuracy));
            } else {
                lblAvgAccuracy.setText("Avg: -");
            }
        } else {
            lblAvgAccuracy.setText("Avg: -");
        }
    }
}

// Weather Report data class
class WeatherReport implements Serializable {
    private static final long serialVersionUID = 1L;
    
    String location;
    int accuracy; // 1-5 stars
    String comment;
    String username;
    LocalDateTime timestamp;
    
    public WeatherReport(String location, int accuracy, String comment, String username, LocalDateTime timestamp) {
        this.location = location;
        this.accuracy = accuracy;
        this.comment = comment;
        this.username = username;
        this.timestamp = timestamp;
    }
}
