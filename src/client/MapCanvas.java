package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class MapCanvas extends JPanel {
    private double latitude = 16.0544;  // Default: Da Nang
    private double longitude = 108.2022;
    private int zoom = 13;
    private String locationName = "";
    
    private Map<String, BufferedImage> tileCache = new HashMap<>();
    private Point dragStart = null;
    private double centerX, centerY;
    
    private static final int TILE_SIZE = 256;
    
    public MapCanvas() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(170, 211, 223)); // Water color
        
        updateCenter();
        
        // Mouse listeners for dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    
                    // Update center based on drag
                    double scale = Math.pow(2, zoom);
                    centerX -= dx / (TILE_SIZE * scale);
                    centerY -= dy / (TILE_SIZE * scale);
                    
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
        
        // Mouse wheel for zoom
        addMouseWheelListener(e -> {
            int oldZoom = zoom;
            if (e.getWheelRotation() < 0) {
                zoom = Math.min(18, zoom + 1);
            } else {
                zoom = Math.max(3, zoom - 1);
            }
            
            if (oldZoom != zoom) {
                updateCenter();
                repaint();
            }
        });
    }
    
    public void setLocation(double lat, double lon, String name) {
        this.latitude = lat;
        this.longitude = lon;
        this.locationName = name != null ? name : "";
        this.zoom = 13;
        updateCenter();
        repaint();
    }
    
    private void updateCenter() {
        // Convert lat/lon to tile coordinates
        double latRad = Math.toRadians(latitude);
        double n = Math.pow(2, zoom);
        centerX = (longitude + 180.0) / 360.0 * n;
        centerY = (1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * n;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        if (locationName.isEmpty()) {
            drawWelcomeScreen(g2, width, height);
            return;
        }
        
        // Calculate visible tiles
        int centerPixelX = width / 2;
        int centerPixelY = height / 2;
        
        int centerTileX = (int) centerX;
        int centerTileY = (int) centerY;
        
        double offsetX = (centerX - centerTileX) * TILE_SIZE;
        double offsetY = (centerY - centerTileY) * TILE_SIZE;
        
        int minTileX = centerTileX - (int) Math.ceil((centerPixelX + offsetX) / TILE_SIZE);
        int maxTileX = centerTileX + (int) Math.ceil((width - centerPixelX + offsetX) / TILE_SIZE);
        int minTileY = centerTileY - (int) Math.ceil((centerPixelY + offsetY) / TILE_SIZE);
        int maxTileY = centerTileY + (int) Math.ceil((height - centerPixelY + offsetY) / TILE_SIZE);
        
        // Draw tiles
        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
                int x = centerPixelX + (tileX - centerTileX) * TILE_SIZE - (int) offsetX;
                int y = centerPixelY + (tileY - centerTileY) * TILE_SIZE - (int) offsetY;
                
                BufferedImage tile = getTile(tileX, tileY, zoom);
                if (tile != null) {
                    g2.drawImage(tile, x, y, TILE_SIZE, TILE_SIZE, null);
                } else {
                    // Draw placeholder
                    g2.setColor(new Color(240, 240, 240));
                    g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        
        // Draw center marker (location pin)
        drawLocationMarker(g2, centerPixelX, centerPixelY);
        
        // Draw controls overlay
        drawControls(g2, width, height);
    }
    
    private void drawWelcomeScreen(Graphics2D g, int width, int height) {
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(102, 126, 234),
            0, height, new Color(118, 75, 162)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);
        
        // White container
        int boxW = 500, boxH = 300;
        int boxX = (width - boxW) / 2;
        int boxY = (height - boxH) / 2;
        
        g.setColor(Color.WHITE);
        g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        
        // Icon
        g.setFont(new Font("Dialog", Font.BOLD, 60));
        g.setColor(new Color(149, 165, 166));
        String icon = "MAP";
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(icon)) / 2;
        g.drawString(icon, x, boxY + 120);
        
        // Title
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.setColor(new Color(44, 62, 80));
        String title = "Interactive Map View";
        fm = g.getFontMetrics();
        x = (width - fm.stringWidth(title)) / 2;
        g.drawString(title, x, boxY + 180);
        
        // Subtitle
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.setColor(new Color(127, 140, 141));
        String subtitle = "Search for a location to view OpenStreetMap";
        fm = g.getFontMetrics();
        x = (width - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, x, boxY + 210);
        
        // Hint
        g.setColor(new Color(236, 240, 241));
        g.fillRoundRect(boxX + 50, boxY + 230, boxW - 100, 40, 10, 10);
        
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.setColor(new Color(52, 73, 94));
        String hint = "Tip: Use mouse wheel to zoom, drag to pan";
        fm = g.getFontMetrics();
        x = (width - fm.stringWidth(hint)) / 2;
        g.drawString(hint, x, boxY + 255);
    }
    
    private void drawLocationMarker(Graphics2D g, int x, int y) {
        // Drop shadow
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x - 12, y + 28, 24, 8);
        
        // Pin body
        g.setColor(new Color(231, 76, 60));
        int[] xPoints = {x, x - 12, x + 12};
        int[] yPoints = {y + 25, y - 5, y - 5};
        g.fillPolygon(xPoints, yPoints, 3);
        g.fillOval(x - 15, y - 20, 30, 30);
        
        // Pin inner circle
        g.setColor(Color.WHITE);
        g.fillOval(x - 8, y - 13, 16, 16);
        
        // Pin border
        g.setColor(new Color(192, 57, 43));
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - 15, y - 20, 30, 30);
    }
    
    private void drawControls(Graphics2D g, int width, int height) {
        // Zoom level indicator
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, height - 40, 100, 30, 8, 8);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString("Zoom: " + zoom, 20, height - 18);
        
        // Coordinates display
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(width - 260, height - 40, 250, 30, 8, 8);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String coords = String.format("%.4f°N, %.4f°E", latitude, longitude);
        g.drawString(coords, width - 245, height - 18);
        
        // Instructions
        g.setColor(new Color(52, 152, 219, 200));
        g.fillRoundRect(10, 10, 280, 60, 8, 8);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString("Mouse Wheel: Zoom In/Out", 20, 30);
        g.drawString("Drag: Pan Map", 20, 50);
    }
    
    private BufferedImage getTile(int tileX, int tileY, int zoom) {
        int maxTile = (1 << zoom) - 1;
        if (tileX < 0 || tileX > maxTile || tileY < 0 || tileY > maxTile) {
            return null;
        }
        
        String key = zoom + "/" + tileX + "/" + tileY;
        
        if (tileCache.containsKey(key)) {
            return tileCache.get(key);
        }
        
        // Load tile asynchronously
        new Thread(() -> {
            try {
                // Use alternative tile server that's more permissive
                String urlStr = String.format(
                    "https://a.tile.openstreetmap.org/%d/%d/%d.png",
                    zoom, tileX, tileY
                );
                
                URL url = new URL(urlStr);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                
                // Set User-Agent to comply with OSM tile usage policy
                conn.setRequestProperty("User-Agent", "WeatherApp/1.0 (Educational Project)");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                
                BufferedImage img = ImageIO.read(conn.getInputStream());
                
                if (img != null) {
                    tileCache.put(key, img);
                    SwingUtilities.invokeLater(() -> repaint());
                }
                
                conn.disconnect();
                
                // Small delay to respect rate limits
                Thread.sleep(50);
            } catch (Exception e) {
                // Silently fail for individual tiles
                System.err.println("Failed to load tile " + key + ": " + e.getMessage());
            }
        }).start();
        
        return null;
    }
}
