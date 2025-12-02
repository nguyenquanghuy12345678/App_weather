package client;

import shared.DBManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages community weather reports backed by SQLite.
 * Provides persistence for user-submitted weather accuracy reports.
 * Can also sync with server if callback is provided.
 */
public class CommunityReportsManager {
    private List<WeatherReport> cachedReports = new ArrayList<>();
    private boolean dbAvailable = true;
    private ServerCallback serverCallback;
    
    public interface ServerCallback {
        void sendAddReport(String location, int accuracy, String comment, String username);
        void requestReports(String location);
    }
    
    public void setServerCallback(ServerCallback callback) {
        this.serverCallback = callback;
    }

    public CommunityReportsManager() {
        loadReportsFromDb();
    }

    /**
     * Add a new community report
     */
    public void addReport(String location, int accuracy, String comment, String username) {
        WeatherReport report = new WeatherReport(
            location, 
            accuracy, 
            comment, 
            username, 
            LocalDateTime.now()
        );
        
        // Send to server if connected
        if (serverCallback != null) {
            serverCallback.sendAddReport(location, accuracy, comment, username);
        }
        
        // Also cache locally
        cachedReports.add(0, report);
    }
    
    public void setReports(List<WeatherReport> reports) {
        this.cachedReports = new ArrayList<>(reports);
    }

    /**
     * Get all reports, optionally filtered by location
     */
    public List<WeatherReport> getReports(String location) {
        if (location == null || location.isEmpty()) {
            return new ArrayList<>(cachedReports);
        }
        
        List<WeatherReport> filtered = new ArrayList<>();
        for (WeatherReport report : cachedReports) {
            if (report.location.equals(location)) {
                filtered.add(report);
            }
        }
        return filtered;
    }

    /**
     * Get all reports
     */
    public List<WeatherReport> getAllReports() {
        return new ArrayList<>(cachedReports);
    }

    /**
     * Clear all reports
     */
    public void clearReports() {
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection(); Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM community_reports");
                cachedReports.clear();
                return;
            } catch (SQLException e) {
                System.err.println("DB error clearReports: " + e.getMessage());
                dbAvailable = false;
            }
        }
        cachedReports.clear();
    }

    /**
     * Delete reports for a specific location
     */
    public void deleteReportsForLocation(String location) {
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection(); 
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM community_reports WHERE location = ?")) {
                ps.setString(1, location);
                ps.executeUpdate();
                loadReportsFromDb();
                return;
            } catch (SQLException e) {
                System.err.println("DB error deleteReportsForLocation: " + e.getMessage());
                dbAvailable = false;
            }
        }
        cachedReports.removeIf(r -> r.location.equals(location));
    }

    /**
     * Get statistics for a location
     */
    public ReportStats getStatsForLocation(String location) {
        List<WeatherReport> reports = getReports(location);
        if (reports.isEmpty()) {
            return new ReportStats(0, 0.0);
        }
        
        int total = reports.size();
        double avgAccuracy = reports.stream()
            .mapToInt(r -> r.accuracy)
            .average()
            .orElse(0.0);
        
        return new ReportStats(total, avgAccuracy);
    }

    private void loadReportsFromDb() {
        cachedReports.clear();
        if (!dbAvailable) return;
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT location, accuracy, comment, username, timestamp FROM community_reports ORDER BY timestamp DESC LIMIT 1000")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("timestamp");
                    cachedReports.add(new WeatherReport(
                        rs.getString("location"),
                        rs.getInt("accuracy"),
                        rs.getString("comment"),
                        rs.getString("username"),
                        ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                    ));
                }
            }
            System.out.println("Loaded " + cachedReports.size() + " community reports from database");
        } catch (SQLException e) {
            System.err.println("DB error loading reports: " + e.getMessage());
            dbAvailable = false;
        }
    }
}

/**
 * Report statistics data class
 */
class ReportStats {
    public final int totalReports;
    public final double averageAccuracy;
    
    public ReportStats(int total, double avg) {
        this.totalReports = total;
        this.averageAccuracy = avg;
    }
}
