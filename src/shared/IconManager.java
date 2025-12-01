package shared;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for loading and managing icons throughout the application
 */
public class IconManager {
    private static final String ICON_PATH = "resources/icons/";
    
    /**
     * Load an icon from the resources folder
     * @param iconName The name of the icon file (e.g., "sun.png")
     * @param size The size to scale the icon to
     * @return ImageIcon scaled to the specified size, or null if not found
     */
    public static ImageIcon loadIcon(String iconName, int size) {
        try {
            ImageIcon icon = resolveIcon(iconName);
            if (icon == null || icon.getIconWidth() == -1) {
                // try default fallback
                icon = resolveIcon("default.png");
            }
            if (icon == null || icon.getIconWidth() == -1) {
                return null;
            }
            Image image = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconName);
            return null;
        }
    }
    
    /**
     * Load an icon without scaling
     * @param iconName The name of the icon file
     * @return ImageIcon in original size, or null if not found
     */
    public static ImageIcon loadIcon(String iconName) {
        try {
            ImageIcon icon = resolveIcon(iconName);
            if (icon == null || icon.getIconWidth() == -1) {
                return null;
            }
            return icon;
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconName);
            return null;
        }
    }

    private static ImageIcon resolveIcon(String iconName) {
        try {
            // 1) Try classpath with both loader patterns
            java.net.URL url = IconManager.class.getClassLoader().getResource(ICON_PATH + iconName);
            if (url == null) {
                url = IconManager.class.getResource("/" + ICON_PATH + iconName);
            }
            if (url != null) {
                return new ImageIcon(url);
            }
            // 2) Try filesystem relative path
            java.io.File file = new java.io.File(ICON_PATH + iconName);
            if (file.exists()) {
                return new ImageIcon(file.getAbsolutePath());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String mapConditionToIcon(String conditionRaw) {
        if (conditionRaw == null) return "default.png";
        String condition = conditionRaw.toLowerCase();
        if (condition.contains("clear") || condition.contains("sunny")) return "sun.png";
        if (condition.contains("partly cloudy")) return "partly_cloudy.png";
        if (condition.contains("cloudy")) return "cloudy.png";
        if (condition.contains("rain") || condition.contains("drizzle")) return "rain.png";
        if (condition.contains("storm") || condition.contains("thunder")) return "storm.png";
        if (condition.contains("snow")) return "snow.png";
        if (condition.contains("fog")) return "fog.png";
        if (condition.contains("not found") || condition.contains("unavailable") || condition.contains("error")) return "error.png";
        return "default.png";
    }
    
    /**
     * Create a JLabel with icon and text
     * @param iconName The icon file name
     * @param text The text to display
     * @param iconSize The size of the icon
     * @return JLabel with icon and text
     */
    public static JLabel createIconLabel(String iconName, String text, int iconSize) {
        JLabel label = new JLabel(text);
        ImageIcon icon = loadIcon(iconName, iconSize);
        if (icon != null) {
            label.setIcon(icon);
        }
        return label;
    }
    
    /**
     * Create a JButton with icon and text
     * @param iconName The icon file name
     * @param text The button text
     * @param iconSize The size of the icon
     * @return JButton with icon and text
     */
    public static JButton createIconButton(String iconName, String text, int iconSize) {
        JButton button = new JButton(text);
        ImageIcon icon = loadIcon(iconName, iconSize);
        if (icon != null) {
            button.setIcon(icon);
        }
        return button;
    }
    
    /**
     * Set icon for an existing JLabel
     * @param label The JLabel to set icon for
     * @param iconName The icon file name
     * @param iconSize The size of the icon
     */
    public static void setLabelIcon(JLabel label, String iconName, int iconSize) {
        ImageIcon icon = loadIcon(iconName, iconSize);
        if (icon != null) {
            label.setIcon(icon);
            label.setText(""); // Remove text when icon is set
        }
    }
}
