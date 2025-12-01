package client;

import server.WeatherData;
import shared.*;
import javax.swing.*;
import java.awt.*;

public class DetailedWeatherPanel extends JPanel {
    private JLabel lblLocation, lblTemperature, lblCondition, lblFeelsLike;
    private JLabel lblHumidity, lblWind, lblPressure, lblUV;
    private JLabel lblCloudCover, lblPrecipitation, lblSunrise, lblSunset;
    private JLabel lblWeatherIcon, lblUpdate;
    private static final int ICON_SIZE = 100;
    
    public DetailedWeatherPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(135, 206, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        lblLocation = new JLabel("--");
        lblLocation.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLocation.setForeground(Color.WHITE);
        lblLocation.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblLocation, BorderLayout.CENTER);
        
        // Main info panel
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        mainPanel.setOpaque(false);
        
        lblWeatherIcon = new JLabel();
        lblWeatherIcon.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
        tempPanel.setOpaque(false);
        
        lblTemperature = new JLabel("--°C");
        lblTemperature.setFont(new Font("Segoe UI", Font.BOLD, 60));
        lblTemperature.setForeground(Color.WHITE);
        lblTemperature.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblCondition = new JLabel("--");
        lblCondition.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblCondition.setForeground(Color.WHITE);
        lblCondition.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblFeelsLike = new JLabel("Feels like: --°C");
        lblFeelsLike.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblFeelsLike.setForeground(new Color(255, 255, 255, 220));
        lblFeelsLike.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        tempPanel.add(lblTemperature);
        tempPanel.add(Box.createVerticalStrut(5));
        tempPanel.add(lblCondition);
        tempPanel.add(Box.createVerticalStrut(3));
        tempPanel.add(lblFeelsLike);
        
        mainPanel.add(lblWeatherIcon);
        mainPanel.add(tempPanel);
        
        // Details grid
        JPanel detailsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        detailsPanel.add(createInfoBox("💧 Humidity", lblHumidity = new JLabel("--")));
        detailsPanel.add(createInfoBox("💨 Wind", lblWind = new JLabel("--")));
        detailsPanel.add(createInfoBox("🌡️ Pressure", lblPressure = new JLabel("--")));
        detailsPanel.add(createInfoBox("☀️ UV Index", lblUV = new JLabel("--")));
        detailsPanel.add(createInfoBox("☁️ Cloud Cover", lblCloudCover = new JLabel("--")));
        detailsPanel.add(createInfoBox("🌧️ Precipitation", lblPrecipitation = new JLabel("--")));
        detailsPanel.add(createInfoBox("🌅 Sunrise", lblSunrise = new JLabel("--")));
        detailsPanel.add(createInfoBox("🌇 Sunset", lblSunset = new JLabel("--")));
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        lblUpdate = new JLabel("Last update: --");
        lblUpdate.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblUpdate.setForeground(new Color(255, 255, 255, 180));
        footerPanel.add(lblUpdate);
        
        // Center container avoids conflicting SOUTH/PAGE_END usage
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(mainPanel, BorderLayout.NORTH);
        centerContainer.add(detailsPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoBox(String title, JLabel valueLabel) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(255, 255, 255, 160));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(Constants.COLOR_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(Constants.COLOR_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(5));
        box.add(valueLabel);
        
        return box;
    }
    
    public void updateWeather(WeatherData data) {
        if (data == null) return;
        
        lblLocation.setText(data.getLocation());
        lblTemperature.setText(String.format("%.1f°C", data.getTemperature()));
        lblCondition.setText(data.getCondition());
        lblFeelsLike.setText(String.format("Feels like: %.1f°C", data.getFeelsLike()));
        
        lblHumidity.setText(data.getHumidity() + "%");
        lblWind.setText(String.format("%.1f km/h %s", data.getWindSpeed(), 
            data.getWindDirection() != null ? data.getWindDirection() : ""));
        lblPressure.setText(String.format("%.0f hPa", data.getPressure()));
        lblUV.setText(String.format("%.1f", data.getUvIndex()));
        lblCloudCover.setText(data.getCloudCover() + "%");
        lblPrecipitation.setText(String.format("%.1f mm", data.getPrecipitation()));
        lblSunrise.setText(data.getSunrise() != null ? data.getSunrise() : "--");
        lblSunset.setText(data.getSunset() != null ? data.getSunset() : "--");
        lblUpdate.setText("Last update: " + data.getLastUpdate());
        
        // Update icon and background
        updateAppearance(data.getCondition());
    }
    
    private void updateAppearance(String condition) {
        String conditionLower = condition.toLowerCase();
        
        String iconName = IconManager.mapConditionToIcon(condition);
        setWeatherIcon(iconName);

        if (conditionLower.contains("clear") || conditionLower.contains("sunny")) {
            setBackground(new Color(135, 206, 250));
        } else if (conditionLower.contains("partly cloudy")) {
            setBackground(new Color(176, 196, 222));
        } else if (conditionLower.contains("cloudy")) {
            setBackground(new Color(169, 169, 169));
        } else if (conditionLower.contains("rain") || conditionLower.contains("drizzle")) {
            setBackground(new Color(119, 136, 153));
        } else if (conditionLower.contains("storm") || conditionLower.contains("thunder")) {
            setBackground(new Color(72, 79, 92));
        } else if (conditionLower.contains("snow")) {
            setBackground(new Color(176, 224, 230));
        } else if (conditionLower.contains("fog")) {
            setBackground(new Color(192, 192, 192));
        } else {
            setBackground(new Color(135, 206, 250));
        }
    }
    
    private void setWeatherIcon(String iconName) {
        ImageIcon icon = IconManager.loadIcon(iconName, ICON_SIZE);
        if (icon != null) {
            lblWeatherIcon.setIcon(icon);
            lblWeatherIcon.setText("");
        } else {
            lblWeatherIcon.setText("☁");
            lblWeatherIcon.setFont(new Font("Segoe UI", Font.PLAIN, ICON_SIZE));
            lblWeatherIcon.setForeground(Color.WHITE);
        }
    }
}
