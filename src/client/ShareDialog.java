package client;

import server.WeatherData;
import shared.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ShareDialog extends JDialog {
    private WeatherData weatherData;
    private JTextArea txtShareText;
    
    public ShareDialog(JFrame parent, WeatherData data) {
        super(parent, "Share Weather", true);
        this.weatherData = data;
        initUI();
    }
    
    private void initUI() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("📤 Share Weather Information");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Share text preview
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblPreview = new JLabel("Preview:");
        lblPreview.setFont(new Font("Segoe UI", Font.BOLD, 14));
        centerPanel.add(lblPreview, BorderLayout.NORTH);
        
        txtShareText = new JTextArea(generateShareText());
        txtShareText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtShareText.setLineWrap(true);
        txtShareText.setWrapStyleWord(true);
        txtShareText.setEditable(true);
        txtShareText.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        JScrollPane scrollPane = new JScrollPane(txtShareText);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        
        JButton btnCopy = createButton("📋 Copy to Clipboard", Constants.COLOR_PRIMARY);
        btnCopy.addActionListener(e -> copyToClipboard());
        
        JButton btnSaveImage = createButton("🖼️ Save as Image", new Color(46, 204, 113));
        btnSaveImage.addActionListener(e -> saveAsImage());
        
        JButton btnSaveText = createButton("💾 Save as Text", new Color(52, 73, 94));
        btnSaveText.addActionListener(e -> saveAsText());
        
        JButton btnClose = createButton("✖ Close", Constants.COLOR_DANGER);
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnCopy);
        buttonPanel.add(btnSaveImage);
        buttonPanel.add(btnSaveText);
        buttonPanel.add(btnClose);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }
    
    private String generateShareText() {
        if (weatherData == null) return "No weather data available";
        
        StringBuilder sb = new StringBuilder();
        sb.append("🌤 WEATHER REPORT\n");
        sb.append("═".repeat(40)).append("\n\n");
        sb.append("📍 Location: ").append(weatherData.getLocation()).append("\n");
        sb.append("🌡️ Temperature: ").append(String.format("%.1f°C", weatherData.getTemperature())).append("\n");
        sb.append("🌡️ Feels Like: ").append(String.format("%.1f°C", weatherData.getFeelsLike())).append("\n");
        sb.append("☁️ Condition: ").append(weatherData.getCondition()).append("\n\n");
        
        sb.append("Details:\n");
        sb.append("─".repeat(40)).append("\n");
        sb.append("💧 Humidity: ").append(weatherData.getHumidity()).append("%\n");
        sb.append("💨 Wind: ").append(String.format("%.1f km/h %s", weatherData.getWindSpeed(), 
            weatherData.getWindDirection() != null ? weatherData.getWindDirection() : "")).append("\n");
        sb.append("🌡️ Pressure: ").append(String.format("%.0f hPa", weatherData.getPressure())).append("\n");
        sb.append("☀️ UV Index: ").append(String.format("%.1f", weatherData.getUvIndex())).append("\n");
        sb.append("☁️ Cloud Cover: ").append(weatherData.getCloudCover()).append("%\n");
        sb.append("🌧️ Precipitation: ").append(String.format("%.1f mm", weatherData.getPrecipitation())).append("\n");
        
        if (weatherData.getSunrise() != null && weatherData.getSunset() != null) {
            sb.append("\n🌅 Sunrise: ").append(weatherData.getSunrise()).append("\n");
            sb.append("🌇 Sunset: ").append(weatherData.getSunset()).append("\n");
        }
        
        sb.append("\n").append("─".repeat(40)).append("\n");
        sb.append("⏰ Updated: ").append(weatherData.getLastUpdate()).append("\n");
        sb.append("\nShared from Weather App 🌦️");
        
        return sb.toString();
    }
    
    private void copyToClipboard() {
        try {
            StringSelection selection = new StringSelection(txtShareText.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            JOptionPane.showMessageDialog(this,
                "Weather information copied to clipboard!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to copy: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveAsText() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Weather Report");
        fileChooser.setSelectedFile(new File("weather_report.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                java.nio.file.Files.writeString(file.toPath(), txtShareText.getText());
                
                JOptionPane.showMessageDialog(this,
                    "Weather report saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Weather Image");
        fileChooser.setSelectedFile(new File("weather_report.png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                
                // Create image from text
                BufferedImage image = new BufferedImage(600, 700, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                
                // Anti-aliasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Background gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(135, 206, 250),
                    0, 700, new Color(70, 130, 180));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, 600, 700);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                String[] lines = txtShareText.getText().split("\n");
                int y = 30;
                for (String line : lines) {
                    if (line.startsWith("🌤") || line.startsWith("Details:")) {
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    } else {
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                    g2d.drawString(line, 30, y);
                    y += 22;
                }
                
                g2d.dispose();
                
                // Save image
                ImageIO.write(image, "png", file);
                
                JOptionPane.showMessageDialog(this,
                    "Weather image saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save image: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
