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
    private DetailedWeatherPanel detailedPanel;
    private ForecastPanel forecastPanel;
    private MapPanel mapPanel;
    private CommunityReportsPanel communityPanel;
    private JButton btnRefresh, btnDisconnect, btnShare, btnFavorite;
    private JLabel lblStatus, lblUsername;
    private JTabbedPane tabbedPane;
    private String username;
    private boolean connected = false;
    private Timer autoRefreshTimer;
    private SearchHistoryManager historyManager;
    private server.WeatherData currentWeatherData;
    
    public WeatherClient() {
        historyManager = new SearchHistoryManager();
        initUI();
        showLoginDialog();
    }
    
    private void initUI() {
        setTitle("Weather Client - Advanced Features");
        
        // Set window icon
        ImageIcon windowIcon = IconManager.loadIcon("cloud_app.png", 32);
        if (windowIcon != null) {
            setIconImage(windowIcon.getImage());
        }
        
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        
        // Top panel - Control bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Constants.COLOR_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Left side - User info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        lblUsername = IconManager.createIconLabel("user.png", " Guest", 16);
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
        
        btnFavorite = createControlButton("⭐ Favorite", new Color(255, 193, 7));
        btnFavorite.addActionListener(e -> toggleFavorite());
        
        btnShare = createControlButton("📤 Share", new Color(46, 204, 113));
        btnShare.addActionListener(e -> shareWeather());
        
        btnRefresh = IconManager.createIconButton("refresh.png", " Refresh", 16);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(Constants.COLOR_PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setEnabled(false);
        
        btnDisconnect = IconManager.createIconButton("disconnect.png", " Disconnect", 16);
        btnDisconnect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDisconnect.setBackground(Constants.COLOR_DANGER);
        btnDisconnect.setForeground(Color.WHITE);
        btnDisconnect.setFocusPainted(false);
        btnDisconnect.setBorderPainted(false);
        btnDisconnect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDisconnect.setEnabled(false);
        
        rightPanel.add(btnFavorite);
        rightPanel.add(btnShare);
        rightPanel.add(btnRefresh);
        rightPanel.add(btnDisconnect);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        // Center - Tabbed pane with multiple views
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Tab 1: Current Weather (original)
        weatherPanel = new WeatherPanel();
        tabbedPane.addTab("🌤 Current", weatherPanel);
        
        // Tab 2: Detailed Weather
        detailedPanel = new DetailedWeatherPanel();
        tabbedPane.addTab("📊 Details", detailedPanel);
        
        // Tab 3: 7-Day Forecast
        forecastPanel = new ForecastPanel();
        tabbedPane.addTab("📅 Forecast", forecastPanel);
        
        // Tab 4: Map
        mapPanel = new MapPanel();
        tabbedPane.addTab("🗺️ Map", mapPanel);
        
        // Tab 5: Community Reports
        communityPanel = new CommunityReportsPanel();
        tabbedPane.addTab("👥 Community", communityPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button actions
        btnRefresh.addActionListener(e -> requestWeather());
        btnDisconnect.addActionListener(e -> disconnect());
        
        // Search listener
        weatherPanel.setSearchListener(e -> searchLocation());
        
        // Auto refresh every 30 seconds
        autoRefreshTimer = new Timer(30000, e -> {
            if (connected) {
                requestWeather();
            }
        });
        
        setLocationRelativeTo(null);
    }
    
    private JButton createControlButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setEnabled(false);
        return btn;
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
                // Update username label with icon
                ImageIcon userIcon = IconManager.loadIcon("user.png", 16);
                if (userIcon != null) {
                    lblUsername.setIcon(userIcon);
                    lblUsername.setText(" " + username);
                } else {
                    lblUsername.setText("👤 " + username);
                }
                lblStatus.setText("● Connected to " + host + ":" + port);
                lblStatus.setForeground(Constants.COLOR_SUCCESS);
                btnRefresh.setEnabled(true);
                btnDisconnect.setEnabled(true);
                btnShare.setEnabled(true);
                btnFavorite.setEnabled(true);
                weatherPanel.setSearchEnabled(true);
                
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
                currentWeatherData = data;
                
                // Update all panels
                weatherPanel.updateWeather(data);
                detailedPanel.updateWeather(data);
                
                // Update forecast
                if (data.getForecast() != null && !data.getForecast().isEmpty()) {
                    forecastPanel.updateForecast(data.getForecast());
                }
                
                // Update map
                String locationName = data.getLocation();
                // Try to find coordinates from search history or use defaults
                LocationData locationData = findLocationData(locationName);
                if (locationData != null) {
                    mapPanel.updateMap(locationData);
                    communityPanel.setLocation(locationName);
                    
                    // Add to search history
                    historyManager.addToHistory(locationData);
                    
                    // Update favorite button
                    updateFavoriteButton(locationName);
                }
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
    
    private void searchLocation() {
        if (!connected) return;
        
        try {
            String selectedLocation = weatherPanel.getSelectedLocation();
            
            if (selectedLocation == null || selectedLocation.trim().isEmpty()) {
                weatherPanel.showError("Vui lòng nhập tên địa điểm");
                return;
            }
            
            // First try to get from predefined locations
            LocationData locationData = getLocationCoordinates(selectedLocation);
            
            // If not found, send the location name to server for geocoding
            if (locationData == null) {
                locationData = new LocationData(selectedLocation, 0, 0); // Server will geocode
            }
            
            Message request = new Message(Constants.MSG_LOCATION_SEARCH, username, locationData);
            out.writeObject(request);
            out.flush();
            
        } catch (IOException e) {
            weatherPanel.showError("Failed to search location");
            e.printStackTrace();
        }
    }
    
    private LocationData getLocationCoordinates(String locationName) {
        // Mapping of location names to their coordinates
        switch (locationName) {
            case "Da Nang, Vietnam":
                return new LocationData("Da Nang, Vietnam", 16.0544, 108.2022);
            case "Ho Chi Minh City, Vietnam":
                return new LocationData("Ho Chi Minh City, Vietnam", 10.8231, 106.6297);
            case "Hanoi, Vietnam":
                return new LocationData("Hanoi, Vietnam", 21.0285, 105.8542);
            case "Tokyo, Japan":
                return new LocationData("Tokyo, Japan", 35.6762, 139.6503);
            case "Seoul, South Korea":
                return new LocationData("Seoul, South Korea", 37.5665, 126.9780);
            case "Bangkok, Thailand":
                return new LocationData("Bangkok, Thailand", 13.7563, 100.5018);
            case "Singapore":
                return new LocationData("Singapore", 1.3521, 103.8198);
            case "New York, USA":
                return new LocationData("New York, USA", 40.7128, -74.0060);
            case "London, UK":
                return new LocationData("London, UK", 51.5074, -0.1278);
            case "Paris, France":
                return new LocationData("Paris, France", 48.8566, 2.3522);
            default:
                return null;
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
            btnShare.setEnabled(false);
            btnFavorite.setEnabled(false);
            weatherPanel.setSearchEnabled(false);
            
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
    
    private LocationData findLocationData(String locationName) {
        // First check predefined locations
        LocationData predefined = getLocationCoordinates(locationName);
        if (predefined != null) return predefined;
        
        // Check search history
        for (LocationData loc : historyManager.getSearchHistory()) {
            if (loc.getLocationName().equals(locationName)) {
                return loc;
            }
        }
        
        // Check favorites
        for (LocationData loc : historyManager.getFavorites()) {
            if (loc.getLocationName().equals(locationName)) {
                return loc;
            }
        }
        
        // Default to Da Nang if not found
        return new LocationData(locationName, 16.0544, 108.2022);
    }
    
    private void shareWeather() {
        if (currentWeatherData == null) {
            JOptionPane.showMessageDialog(this,
                "No weather data to share!",
                "No Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ShareDialog dialog = new ShareDialog(this, currentWeatherData);
        dialog.setVisible(true);
    }
    
    private void toggleFavorite() {
        if (currentWeatherData == null) return;
        
        String locationName = currentWeatherData.getLocation();
        LocationData locationData = findLocationData(locationName);
        
        if (historyManager.isFavorite(locationName)) {
            historyManager.removeFromFavorites(locationName);
            btnFavorite.setText("⭐ Favorite");
            btnFavorite.setBackground(new Color(255, 193, 7));
            JOptionPane.showMessageDialog(this,
                "Removed from favorites",
                "Favorites",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            historyManager.addToFavorites(locationData);
            btnFavorite.setText("★ Favorited");
            btnFavorite.setBackground(new Color(255, 152, 0));
            JOptionPane.showMessageDialog(this,
                "Added to favorites",
                "Favorites",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateFavoriteButton(String locationName) {
        if (historyManager.isFavorite(locationName)) {
            btnFavorite.setText("★ Favorited");
            btnFavorite.setBackground(new Color(255, 152, 0));
        } else {
            btnFavorite.setText("⭐ Favorite");
            btnFavorite.setBackground(new Color(255, 193, 7));
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