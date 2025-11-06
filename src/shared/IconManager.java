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
            ImageIcon icon = new ImageIcon(ICON_PATH + iconName);
            if (icon.getIconWidth() == -1) {
                // Icon not found, return null
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
            ImageIcon icon = new ImageIcon(ICON_PATH + iconName);
            if (icon.getIconWidth() == -1) {
                return null;
            }
            return icon;
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconName);
            return null;
        }
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
