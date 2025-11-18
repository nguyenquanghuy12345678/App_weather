package client;

import shared.LocationData;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;

public class MapPanel extends JPanel {
    private JEditorPane mapView;
    private JLabel lblLocation;
    private LocationData currentLocation;
    
    public MapPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        lblLocation = new JLabel("Map View");
        lblLocation.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLocation.setForeground(shared.Constants.COLOR_DARK);
        lblLocation.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton btnOpenExternal = new JButton("🗺️ Open in Browser");
        btnOpenExternal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnOpenExternal.setBackground(shared.Constants.COLOR_PRIMARY);
        btnOpenExternal.setForeground(Color.WHITE);
        btnOpenExternal.setFocusPainted(false);
        btnOpenExternal.addActionListener(e -> openInBrowser());
        
        headerPanel.add(lblLocation, BorderLayout.WEST);
        headerPanel.add(btnOpenExternal, BorderLayout.EAST);
        
        // Map view using HTML with Leaflet/OpenStreetMap
        mapView = new JEditorPane();
        mapView.setContentType("text/html");
        mapView.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(mapView);
        scrollPane.setBorder(BorderFactory.createLineBorder(shared.Constants.COLOR_PRIMARY, 2));
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Show default map
        updateMap(null);
    }
    
    public void updateMap(LocationData location) {
        this.currentLocation = location;
        
        double lat = location != null ? location.getLatitude() : 16.0544;
        double lon = location != null ? location.getLongitude() : 108.2022;
        String name = location != null ? location.getLocationName() : "Da Nang, Vietnam";
        
        lblLocation.setText("Map: " + name);
        
        String html = generateMapHTML(lat, lon, name);
        mapView.setText(html);
        mapView.setCaretPosition(0);
    }
    
    private String generateMapHTML(double lat, double lon, String name) {
        return String.format(
            "<html>" +
            "<head>" +
            "<meta charset='utf-8'>" +
            "<style>" +
            "body { margin: 0; padding: 20px; font-family: 'Segoe UI', Arial, sans-serif; background: #f0f0f0; }" +
            ".map-container { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".map-info { background: #3498db; color: white; padding: 15px; border-radius: 5px; margin-bottom: 15px; }" +
            ".map-placeholder { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); height: 400px; " +
            "border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white; " +
            "font-size: 18px; text-align: center; }" +
            ".coords { background: rgba(255,255,255,0.2); padding: 10px; border-radius: 5px; margin-top: 10px; }" +
            ".action-buttons { margin-top: 15px; }" +
            ".btn { background: #2ecc71; color: white; padding: 10px 20px; border-radius: 5px; " +
            "text-decoration: none; display: inline-block; margin-right: 10px; }" +
            ".btn:hover { background: #27ae60; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class='map-container'>" +
            "<div class='map-info'>" +
            "<h2 style='margin: 0 0 10px 0;'>📍 %s</h2>" +
            "<div class='coords'>" +
            "<strong>Coordinates:</strong> %.4f°N, %.4f°E" +
            "</div>" +
            "</div>" +
            "<div class='map-placeholder'>" +
            "<div>" +
            "<div style='font-size: 64px; margin-bottom: 20px;'>🗺️</div>" +
            "<div>Interactive Map View</div>" +
            "<div style='font-size: 14px; margin-top: 10px; opacity: 0.9;'>" +
            "Click 'Open in Browser' button above for full map features" +
            "</div>" +
            "</div>" +
            "</div>" +
            "<div class='action-buttons'>" +
            "<a class='btn' href='https://www.openstreetmap.org/?mlat=%.4f&mlon=%.4f#map=13/%.4f/%.4f' " +
            "target='_blank'>🌍 OpenStreetMap</a>" +
            "<a class='btn' href='https://www.google.com/maps/@%.4f,%.4f,13z' " +
            "target='_blank'>🗺️ Google Maps</a>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>",
            escapeHtml(name), lat, lon, lat, lon, lat, lon, lat, lon
        );
    }
    
    private void openInBrowser() {
        if (currentLocation == null) return;
        
        try {
            // Create temporary HTML file with interactive map
            File tempFile = File.createTempFile("weather_map_", ".html");
            tempFile.deleteOnExit();
            
            String html = generateInteractiveMapHTML(
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                currentLocation.getLocationName()
            );
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(html);
            }
            
            // Open in default browser
            Desktop.getDesktop().browse(tempFile.toURI());
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Could not open map in browser: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateInteractiveMapHTML(double lat, double lon, String name) {
        return String.format(
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset='utf-8'>" +
            "<title>Weather Map - %s</title>" +
            "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' />" +
            "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
            "<style>" +
            "body { margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; }" +
            "#map { height: 100vh; width: 100%%; }" +
            ".info-panel { position: absolute; top: 10px; right: 10px; z-index: 1000; " +
            "background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.2); }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div id='map'></div>" +
            "<div class='info-panel'>" +
            "<h3 style='margin: 0 0 10px 0;'>📍 %s</h3>" +
            "<div><strong>Lat:</strong> %.4f°</div>" +
            "<div><strong>Lon:</strong> %.4f°</div>" +
            "</div>" +
            "<script>" +
            "var map = L.map('map').setView([%.4f, %.4f], 13);" +
            "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
            "  maxZoom: 19," +
            "  attribution: '© OpenStreetMap contributors'" +
            "}).addTo(map);" +
            "var marker = L.marker([%.4f, %.4f]).addTo(map);" +
            "marker.bindPopup('<b>%s</b><br>%.4f°N, %.4f°E').openPopup();" +
            "</script>" +
            "</body>" +
            "</html>",
            escapeHtml(name), escapeHtml(name), lat, lon, lat, lon, lat, lon, 
            escapeHtml(name), lat, lon
        );
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
