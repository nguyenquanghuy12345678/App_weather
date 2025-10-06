package client;

import server.WeatherData;
import shared.*;
import javax.swing.*;
import java.awt.*;

public class WeatherPanel extends JPanel {
    private JLabel lblLocation, lblTemperature, lblCondition, lblHumidity, lblWind, lblUpdate;
    private JLabel lblWeatherIcon;
    
    public WeatherPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(135, 206, 250)); // Sky blue
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        lblLocation = new JLabel("Weather Information");
        lblLocation.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLocation.setForeground(Color.WHITE);
        headerPanel.add(lblLocation);
        
        // Main weather display
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Weather icon and temperature
        JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        tempPanel.setOpaque(false);
        
        lblWeatherIcon = new JLabel("☀");
        lblWeatherIcon.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        
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
    
    public void updateWeather(WeatherData data) {
        if (data == null) return;
        
        lblLocation.setText(data.getLocation());
        lblTemperature.setText(String.format("%.1f°C", data.getTemperature()));
        lblCondition.setText(data.getCondition());
        lblHumidity.setText(data.getHumidity() + "%");
        lblWind.setText(String.format("%.1f km/h", data.getWindSpeed()));
        lblUpdate.setText("Last update: " + data.getLastUpdate());
        
        // Update icon based on condition
        switch (data.getCondition().toLowerCase()) {
            case "sunny":
                lblWeatherIcon.setText("☀");
                setBackground(new Color(135, 206, 250));
                break;
            case "partly cloudy":
                lblWeatherIcon.setText("⛅");
                setBackground(new Color(176, 196, 222));
                break;
            case "cloudy":
                lblWeatherIcon.setText("☁");
                setBackground(new Color(169, 169, 169));
                break;
            case "rainy":
                lblWeatherIcon.setText("🌧");
                setBackground(new Color(119, 136, 153));
                break;
            case "stormy":
                lblWeatherIcon.setText("⛈");
                setBackground(new Color(72, 79, 92));
                break;
            default:
                lblWeatherIcon.setText("🌤");
        }
    }
    
    public void showError(String message) {
        lblCondition.setText(message);
        lblTemperature.setText("--°C");
        lblHumidity.setText("N/A");
        lblWind.setText("N/A");
    }
}