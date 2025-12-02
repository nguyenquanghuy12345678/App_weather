package client;

import shared.LocationData;
import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class MapPanel extends JPanel {
    private MapCanvas mapCanvas;
    private JLabel lblTitle;
    private JLabel lblCoords;
    private String currentLocation;
    private double latitude;
    private double longitude;
    
    public MapPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        lblTitle = new JLabel("Map View");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        
        lblCoords = new JLabel("Select a location");
        lblCoords.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCoords.setForeground(new Color(255, 255, 255, 200));
        
        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblCoords, BorderLayout.SOUTH);
        
        JButton btnOpenBrowser = new JButton("Open in Browser");
        btnOpenBrowser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOpenBrowser.setBackground(new Color(46, 204, 113));
        btnOpenBrowser.setForeground(Color.WHITE);
        btnOpenBrowser.setFocusPainted(false);
        btnOpenBrowser.setBorderPainted(false);
        btnOpenBrowser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenBrowser.addActionListener(e -> openInBrowser());
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnOpenBrowser, BorderLayout.EAST);
        
        // Map canvas - interactive OpenStreetMap
        mapCanvas = new MapCanvas();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mapCanvas, BorderLayout.CENTER);
    }
    
    public void updateMap(LocationData location) {
        if (location == null) {
            this.currentLocation = null;
            this.latitude = 0;
            this.longitude = 0;
            lblTitle.setText("Map View");
            lblCoords.setText("Select a location");
            mapCanvas.setLocation(0, 0, null);
            return;
        }
        
        this.currentLocation = location.getLocationName();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        
        lblTitle.setText("Map: " + currentLocation);
        lblCoords.setText(String.format("%.4f°N, %.4f°E", latitude, longitude));
        
        mapCanvas.setLocation(latitude, longitude, currentLocation);
    }
    
    private void openInBrowser() {
        if (currentLocation != null) {
            try {
                String url = String.format(
                    "https://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f#map=13/%.6f/%.6f",
                    latitude, longitude, latitude, longitude
                );
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Cannot open browser: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please search for a location first",
                "No Location",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
