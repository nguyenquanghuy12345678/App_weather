package client;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages community weather reports.
 * Uses in-memory cache and syncs with server.
 * NO LOCAL DATABASE - all data from server.
 */
public class CommunityReportsManager {
    private List<WeatherReport> cachedReports = new ArrayList<>();
    private ServerCallback serverCallback;
    
    public interface ServerCallback {
        void sendAddReport(String location, int accuracy, String comment, String username);
        void requestReports(String location);
    }
    
    public void setServerCallback(ServerCallback callback) {
        this.serverCallback = callback;
    }

    public CommunityReportsManager() {
        // Do NOT load from local DB - wait for server data
    }

    /**
     * Add a new community report
     */
    public void addReport(String location, int accuracy, String comment, String username) {
        // ONLY send to server if connected
        if (serverCallback != null) {
            serverCallback.sendAddReport(location, accuracy, comment, username);
        } else {
            System.err.println("WARNING: Not connected to server. Report NOT saved!");
        }
        
        // Add to local cache for immediate display
        WeatherReport report = new WeatherReport(
            location, 
            accuracy, 
            comment, 
            username, 
            LocalDateTime.now()
        );
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
     * Clear all reports (cache only - server data unchanged)
     */
    public void clearReports() {
        cachedReports.clear();
    }

    /**
     * Delete reports for a specific location (cache only)
     */
    /**
     * Delete reports for a specific location (cache only)
     */
    public void deleteReportsForLocation(String location) {
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
