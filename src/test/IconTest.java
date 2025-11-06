package test;

import javax.swing.*;
import java.awt.*;

/**
 * Test class to verify weather icons display correctly
 * Run this to check if all icons are loaded properly
 */
public class IconTest extends JFrame {
    
    public IconTest() {
        setTitle("Weather Icons Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3, 10, 10));
        getContentPane().setBackground(Color.WHITE);
        
        String[] iconFiles = {
            "resources/icons/sun.png",
            "resources/icons/partly_cloudy.png",
            "resources/icons/cloudy.png",
            "resources/icons/rain.png",
            "resources/icons/storm.png",
            "resources/icons/snow.png",
            "resources/icons/fog.png",
            "resources/icons/error.png",
            "resources/icons/default.png"
        };
        
        String[] labels = {
            "Sunny",
            "Partly Cloudy",
            "Cloudy",
            "Rain",
            "Storm",
            "Snow",
            "Fog",
            "Error",
            "Default"
        };
        
        for (int i = 0; i < iconFiles.length; i++) {
            JPanel panel = createIconPanel(iconFiles[i], labels[i]);
            add(panel);
        }
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createIconPanel(String iconPath, String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panel.setPreferredSize(new Dimension(150, 150));
        
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            iconLabel.setText("❌ Not Found");
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        
        JLabel textLabel = new JLabel(label);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new IconTest();
        });
    }
}
