package client;

import shared.LocationData;
import shared.DBManager;

import java.sql.*;
import java.util.*;

/**
 * Manages search history and favorites.
 * Search history: Local DB (client-side only)
 * Favorites: Server sync (NO local DB for favorites)
 */
public class SearchHistoryManager {
    private static final int MAX_HISTORY = 20;

    private List<LocationData> cacheHistory = new ArrayList<>();
    private List<LocationData> cacheFavorites = new ArrayList<>();
    private boolean dbAvailable = true;
    private ServerCallback serverCallback;
    
    public interface ServerCallback {
        void sendAddFavorite(LocationData location);
        void sendRemoveFavorite(String location);
    }
    
    public void setServerCallback(ServerCallback callback) {
        this.serverCallback = callback;
    }

    public SearchHistoryManager() {
        init();
    }

    private void init() {
        try (Connection conn = DBManager.getConnection()) {
            loadHistoryFromDb(conn);
            // DO NOT load favorites from DB - wait for server data
        } catch (SQLException e) {
            System.err.println("SQLite not available: " + e.getMessage() + " (using in-memory fallback)");
            dbAvailable = false;
        }
    }

    // Search History methods
    public void addToHistory(LocationData location) {
        if (location == null) return;
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO search_history(location, latitude, longitude, last_access) VALUES (?,?,?,CURRENT_TIMESTAMP) " +
                                "ON CONFLICT(location) DO UPDATE SET latitude=excluded.latitude, longitude=excluded.longitude, last_access=CURRENT_TIMESTAMP")) {
                    ps.setString(1, location.getLocationName());
                    ps.setDouble(2, location.getLatitude());
                    ps.setDouble(3, location.getLongitude());
                    ps.executeUpdate();
                }
                pruneHistory(conn);
                loadHistoryFromDb(conn);
                return;
            } catch (SQLException e) {
                System.err.println("DB error addToHistory: " + e.getMessage());
                dbAvailable = false; // degrade
            }
        }
        // Fallback in-memory
        cacheHistory.removeIf(loc -> loc.getLocationName().equals(location.getLocationName()));
        cacheHistory.add(0, location);
        if (cacheHistory.size() > MAX_HISTORY) {
            cacheHistory = cacheHistory.subList(0, MAX_HISTORY);
        }
    }

    public List<LocationData> getSearchHistory() {
        return new ArrayList<>(cacheHistory);
    }

    public void clearHistory() {
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection(); Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM search_history");
                cacheHistory.clear();
                return;
            } catch (SQLException e) {
                System.err.println("DB error clearHistory: " + e.getMessage());
                dbAvailable = false;
            }
        }
        cacheHistory.clear();
    }

    // Favorites methods
    public void addToFavorites(LocationData location) {
        if (location == null) return;
        
        // Send to server if connected
        if (serverCallback != null) {
            serverCallback.sendAddFavorite(location);
        }
        
        // Also cache locally
        if (!isFavorite(location.getLocationName())) {
            cacheFavorites.add(0, location);
        }
    }

    public void removeFromFavorites(String locationName) {
        if (locationName == null) return;
        
        // Send to server if connected
        if (serverCallback != null) {
            serverCallback.sendRemoveFavorite(locationName);
        }
        
        // Also remove from local cache
        cacheFavorites.removeIf(loc -> loc.getLocationName().equals(locationName));
    }
    
    public void setFavorites(List<LocationData> favorites) {
        this.cacheFavorites = new ArrayList<>(favorites);
    }

    public boolean isFavorite(String locationName) {
        return cacheFavorites.stream().anyMatch(loc -> loc.getLocationName().equals(locationName));
    }

    public List<LocationData> getFavorites() {
        return new ArrayList<>(cacheFavorites);
    }

    public void clearFavorites() {
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection(); Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM favorites");
                cacheFavorites.clear();
                return;
            } catch (SQLException e) {
                System.err.println("DB error clearFavorites: " + e.getMessage());
                dbAvailable = false;
            }
        }
        cacheFavorites.clear();
    }

    // Internal loaders
    private void loadHistoryFromDb(Connection conn) throws SQLException {
        cacheHistory.clear();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT location, latitude, longitude FROM search_history ORDER BY last_access DESC LIMIT ?")) {
            ps.setInt(1, MAX_HISTORY);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cacheHistory.add(new LocationData(
                            rs.getString("location"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude")
                    ));
                }
            }
        }
        System.out.println("Loaded " + cacheHistory.size() + " history items");
    }

    // loadFavoritesFromDb() REMOVED - favorites come from server only

    private void pruneHistory(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY last_access DESC LIMIT " + MAX_HISTORY + ")");
        }
    }
}
