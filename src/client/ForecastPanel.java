package client;

import server.*;
import shared.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ForecastPanel extends JPanel {
    private JPanel forecastCards;
    private ChartPanel temperatureChart;
    
    public ForecastPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel lblTitle = new JLabel("7-Day Forecast");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Constants.COLOR_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(lblTitle, BorderLayout.NORTH);
        
        // Chart panel
        temperatureChart = new ChartPanel();
        temperatureChart.setPreferredSize(new Dimension(700, 200));
        temperatureChart.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 2),
            "Temperature Trend (°C)",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), Constants.COLOR_DARK));
        
        // Forecast cards container
        forecastCards = new JPanel(new GridLayout(1, 7, 10, 0));
        forecastCards.setOpaque(false);
        forecastCards.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Center panel with chart and cards
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(temperatureChart, BorderLayout.NORTH);
        centerPanel.add(forecastCards, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    public void updateForecast(List<DailyForecast> forecast) {
        if (forecast == null || forecast.isEmpty()) return;
        
        forecastCards.removeAll();
        
        // Update chart
        double[] maxTemps = new double[forecast.size()];
        double[] minTemps = new double[forecast.size()];
        String[] labels = new String[forecast.size()];
        
        for (int i = 0; i < forecast.size(); i++) {
            DailyForecast day = forecast.get(i);
            maxTemps[i] = day.getMaxTemp();
            minTemps[i] = day.getMinTemp();
            labels[i] = formatDate(day.getDate());
            
            // Create forecast card
            forecastCards.add(createForecastCard(day));
        }
        
        temperatureChart.setData(maxTemps, minTemps, labels);
        
        forecastCards.revalidate();
        forecastCards.repaint();
    }
    
    private JPanel createForecastCard(DailyForecast day) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        
        // Date
        JLabel lblDate = new JLabel(formatDate(day.getDate()));
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDate.setForeground(Constants.COLOR_DARK);
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Weather icon (emoji based on condition string)
        String emoji = getWeatherEmoji(day.getCondition());
        JLabel lblIcon = new JLabel(emoji, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Dialog", Font.PLAIN, 48));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Temperature
        JLabel lblMaxTemp = new JLabel(String.format("%.0f°", day.getMaxTemp()));
        lblMaxTemp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMaxTemp.setForeground(new Color(220, 53, 69));
        lblMaxTemp.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblMinTemp = new JLabel(String.format("%.0f°", day.getMinTemp()));
        lblMinTemp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMinTemp.setForeground(new Color(13, 110, 253));
        lblMinTemp.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Condition
        JLabel lblCondition = new JLabel("<html><center>" + day.getCondition() + "</center></html>");
        lblCondition.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblCondition.setForeground(Color.GRAY);
        lblCondition.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Precipitation
        if (day.getPrecipitation() > 0) {
            JLabel lblRain = new JLabel(String.format("💧 %.1fmm", day.getPrecipitation()));
            lblRain.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblRain.setForeground(new Color(13, 110, 253));
            lblRain.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblRain);
        }
        
        card.add(lblDate);
        card.add(Box.createVerticalStrut(5));
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(5));
        card.add(lblMaxTemp);
        card.add(lblMinTemp);
        card.add(Box.createVerticalStrut(3));
        card.add(lblCondition);
        
        return card;
    }
    
    private String formatDate(String isoDate) {
        try {
            // Convert 2024-01-15 to Mon 15
            String[] parts = isoDate.split("-");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[2]);
                int month = Integer.parseInt(parts[1]);
                return String.format("%d/%d", day, month);
            }
            return isoDate;
        } catch (Exception e) {
            return isoDate;
        }
    }
    
    private String getWeatherEmoji(String condition) {
        if (condition == null) return "🌡️";
        String lower = condition.toLowerCase();
        if (lower.contains("clear") || lower.contains("sunny")) return "☀️";
        if (lower.contains("partly")) return "⛅";
        if (lower.contains("cloud")) return "☁️";
        if (lower.contains("rain") || lower.contains("shower")) return "🌧️";
        if (lower.contains("storm")) return "⛈️";
        if (lower.contains("snow")) return "❄️";
        if (lower.contains("fog")) return "🌫️";
        if (lower.contains("wind")) return "💨";
        return "🌡️";
    }
}

// Simple chart component for temperature trend
class ChartPanel extends JPanel {
    private double[] maxTemps;
    private double[] minTemps;
    private String[] labels;
    
    public ChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 200));
    }
    
    public void setData(double[] maxTemps, double[] minTemps, String[] labels) {
        this.maxTemps = maxTemps;
        this.minTemps = minTemps;
        this.labels = labels;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (maxTemps == null || maxTemps.length == 0) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            g.drawString("No forecast data available", getWidth()/2 - 80, getHeight()/2);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int padding = 40;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;
        
        // Find min/max for scaling
        double minVal = Double.MAX_VALUE;
        double maxVal = Double.MIN_VALUE;
        
        for (double temp : maxTemps) {
            if (temp > maxVal) maxVal = temp;
            if (temp < minVal) minVal = temp;
        }
        for (double temp : minTemps) {
            if (temp > maxVal) maxVal = temp;
            if (temp < minVal) minVal = temp;
        }
        
        double range = maxVal - minVal;
        if (range == 0) range = 1;
        
        // Draw grid lines
        g2d.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 5; i++) {
            int y = padding + (height * i / 5);
            g2d.drawLine(padding, y, padding + width, y);
            
            // Y-axis labels
            double tempValue = maxVal - (range * i / 5);
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.drawString(String.format("%.0f°", tempValue), 5, y + 4);
            g2d.setColor(new Color(230, 230, 230));
        }
        
        // Calculate points
        int[] maxX = new int[maxTemps.length];
        int[] maxY = new int[maxTemps.length];
        int[] minX = new int[minTemps.length];
        int[] minY = new int[minTemps.length];
        
        for (int i = 0; i < maxTemps.length; i++) {
            int x = padding + (width * i / (maxTemps.length - 1));
            int yMax = padding + (int)(height - (maxTemps[i] - minVal) / range * height);
            int yMin = padding + (int)(height - (minTemps[i] - minVal) / range * height);
            
            maxX[i] = x;
            maxY[i] = yMax;
            minX[i] = x;
            minY[i] = yMin;
            
            // Draw X-axis labels
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.drawString(labels[i], x - 15, padding + height + 20);
        }
        
        // Draw max temp line (red)
        g2d.setColor(new Color(220, 53, 69));
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < maxX.length - 1; i++) {
            g2d.drawLine(maxX[i], maxY[i], maxX[i + 1], maxY[i + 1]);
        }
        
        // Draw max temp points and values
        for (int i = 0; i < maxX.length; i++) {
            g2d.fillOval(maxX[i] - 4, maxY[i] - 4, 8, 8);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2d.drawString(String.format("%.0f", maxTemps[i]), maxX[i] - 10, maxY[i] - 8);
        }
        
        // Draw min temp line (blue)
        g2d.setColor(new Color(13, 110, 253));
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < minX.length - 1; i++) {
            g2d.drawLine(minX[i], minY[i], minX[i + 1], minY[i + 1]);
        }
        
        // Draw min temp points and values
        for (int i = 0; i < minX.length; i++) {
            g2d.fillOval(minX[i] - 4, minY[i] - 4, 8, 8);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2d.drawString(String.format("%.0f", minTemps[i]), minX[i] - 10, minY[i] + 18);
        }
        
        // Legend
        g2d.setColor(new Color(220, 53, 69));
        g2d.fillRect(padding, 10, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Max Temp", padding + 20, 22);
        
        g2d.setColor(new Color(13, 110, 253));
        g2d.fillRect(padding + 100, 10, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Min Temp", padding + 120, 22);
    }
}
