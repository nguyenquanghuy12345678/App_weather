package client;

import server.WeatherData;
import shared.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WeatherPanel extends JPanel {
    private JLabel lblLocation, lblTemperature, lblCondition, lblHumidity, lblWind, lblUpdate;
    private JLabel lblWeatherIcon;
    private JComboBox<String> cboLocation;
    private JButton btnSearch;
    private static final int ICON_SIZE = 120;
    
    public WeatherPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(135, 206, 250)); // Sky blue
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with search
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);
        
        lblLocation = new JLabel("Weather Information");
        lblLocation.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLocation.setForeground(Color.WHITE);
        lblLocation.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchPanel.setOpaque(false);
        
        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        // Predefined locations
        String[] locations = {
            "Da Nang, Vietnam",
            "Ho Chi Minh City, Vietnam",
            "Hanoi, Vietnam",
            "Tokyo, Japan",
            "Seoul, South Korea",
            "Bangkok, Thailand",
            "Singapore",
            "New York, USA",
            "London, UK",
            "Paris, France"
        };
        
        cboLocation = new JComboBox<>(locations);
        cboLocation.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboLocation.setPreferredSize(new Dimension(250, 35));
        cboLocation.setBackground(Color.WHITE);
        cboLocation.setEnabled(false);
        cboLocation.setEditable(true); // Allow user to type custom location
        
        // Add placeholder-like behavior
        JTextField editor = (JTextField) cboLocation.getEditor().getEditorComponent();
        editor.setForeground(Color.GRAY);
        editor.setText("Nhập tên thành phố...");
        
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setBackground(Constants.COLOR_PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(100, 35));
        btnSearch.setEnabled(false);
        
        searchPanel.add(lblSearchIcon);
        searchPanel.add(cboLocation);
        searchPanel.add(btnSearch);
        
        headerPanel.add(lblLocation, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        
        // Main weather display
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Weather icon and temperature
        JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        tempPanel.setOpaque(false);
        
        lblWeatherIcon = new JLabel();
        lblWeatherIcon.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        setWeatherIcon("resources/icons/sun.png"); // Default icon
        
        lblTemperature = new JLabel("--°C");
        lblTemperature.setFont(new Font("Segoe UI", Font.BOLD, 80));
        lblTemperature.setForeground(Color.WHITE);
        
        tempPanel.add(lblWeatherIcon);
        tempPanel.add(lblTemperature);
        
        // Condition
        lblCondition = new JLabel("Loading...");
        lblCondition.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        lblCondition.setForeground(Color.WHITE);
        lblCondition.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(tempPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblCondition);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 10, 50));
        
        // Humidity box
        JPanel humidityBox = new JPanel();
        humidityBox.setLayout(new BoxLayout(humidityBox, BoxLayout.Y_AXIS));
        humidityBox.setBackground(new Color(255, 255, 255, 180));
        humidityBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel lblHumidityTitle = new JLabel("💧 Humidity");
        lblHumidityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHumidityTitle.setForeground(Constants.COLOR_DARK);
        lblHumidityTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblHumidity = new JLabel("N/A");
        lblHumidity.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHumidity.setForeground(Constants.COLOR_PRIMARY);
        lblHumidity.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        humidityBox.add(lblHumidityTitle);
        humidityBox.add(Box.createVerticalStrut(10));
        humidityBox.add(lblHumidity);
        
        // Wind box
        JPanel windBox = new JPanel();
        windBox.setLayout(new BoxLayout(windBox, BoxLayout.Y_AXIS));
        windBox.setBackground(new Color(255, 255, 255, 180));
        windBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel lblWindTitle = new JLabel("💨 Wind Speed");
        lblWindTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWindTitle.setForeground(Constants.COLOR_DARK);
        lblWindTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblWind = new JLabel("N/A");
        lblWind.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWind.setForeground(Constants.COLOR_PRIMARY);
        lblWind.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        windBox.add(lblWindTitle);
        windBox.add(Box.createVerticalStrut(10));
        windBox.add(lblWind);
        
        detailsPanel.add(humidityBox);
        detailsPanel.add(windBox);
        
        // Footer - Last update
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        lblUpdate = new JLabel("Last update: N/A");
        lblUpdate.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblUpdate.setForeground(new Color(255, 255, 255, 200));
        footerPanel.add(lblUpdate);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.PAGE_END);
    }
    
    private void setWeatherIcon(String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image image = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            lblWeatherIcon.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            // Fallback to text if icon not found
            lblWeatherIcon.setText("?");
            lblWeatherIcon.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        }
    }
    
    public void updateWeather(WeatherData data) {
        if (data == null) return;
        
        lblLocation.setText(data.getLocation());
        lblTemperature.setText(String.format("%.1f°C", data.getTemperature()));
        lblCondition.setText(data.getCondition());
        lblHumidity.setText(data.getHumidity() + "%");
        lblWind.setText(String.format("%.1f km/h", data.getWindSpeed()));
        lblUpdate.setText("Last update: " + data.getLastUpdate());
        
        // Update icon based on condition
        String condition = data.getCondition().toLowerCase();
        if (condition.contains("clear") || condition.contains("sunny")) {
            setWeatherIcon("resources/icons/sun.png");
            setBackground(new Color(135, 206, 250));
        } else if (condition.contains("partly cloudy")) {
            setWeatherIcon("resources/icons/partly_cloudy.png");
            setBackground(new Color(176, 196, 222));
        } else if (condition.contains("cloudy")) {
            setWeatherIcon("resources/icons/cloudy.png");
            setBackground(new Color(169, 169, 169));
        } else if (condition.contains("rain") || condition.contains("drizzle")) {
            setWeatherIcon("resources/icons/rain.png");
            setBackground(new Color(119, 136, 153));
        } else if (condition.contains("storm") || condition.contains("thunder")) {
            setWeatherIcon("resources/icons/storm.png");
            setBackground(new Color(72, 79, 92));
        } else if (condition.contains("snow")) {
            setWeatherIcon("resources/icons/snow.png");
            setBackground(new Color(176, 224, 230));
        } else if (condition.contains("fog")) {
            setWeatherIcon("resources/icons/fog.png");
            setBackground(new Color(192, 192, 192));
        } else if (condition.contains("not found") || condition.contains("unavailable") || condition.contains("error")) {
            setWeatherIcon("resources/icons/error.png");
            setBackground(new Color(220, 220, 220));
        } else {
            setWeatherIcon("resources/icons/default.png");
            setBackground(new Color(135, 206, 250));
        }
    }
    
    public void showError(String message) {
        lblCondition.setText(message);
        lblTemperature.setText("--°C");
        lblHumidity.setText("N/A");
        lblWind.setText("N/A");
    }
    
    public void setSearchListener(ActionListener listener) {
        // Remove old listener if exists
        for (ActionListener al : btnSearch.getActionListeners()) {
            btnSearch.removeActionListener(al);
        }
        // Add new listener
        btnSearch.addActionListener(listener);
    }
    
    public String getSelectedLocation() {
        Object selected = cboLocation.getEditor().getItem();
        if (selected != null) {
            String location = selected.toString().trim();
            // Skip placeholder text
            if (!location.isEmpty() && !location.equals("Nhập tên thành phố...")) {
                return location;
            }
        }
        return (String) cboLocation.getSelectedItem();
    }
    
    public void setSearchEnabled(boolean enabled) {
        btnSearch.setEnabled(enabled);
        cboLocation.setEnabled(enabled);
    }
}