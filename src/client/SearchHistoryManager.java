package client;

import shared.LocationData;
import shared.DBManager;

import java.sql.*;
import java.util.*;

/**
 * Manages search history and favorites backed by SQLite instead of serialized files.
 * Falls back to in-memory lists if database unavailable.
 */
public class SearchHistoryManager {
    private static final int MAX_HISTORY = 20;

    private List<LocationData> cacheHistory = new ArrayList<>();
    private List<LocationData> cacheFavorites = new ArrayList<>();
    private boolean dbAvailable = true;

    public SearchHistoryManager() {
        init();
    }

    private void init() {
        try (Connection conn = DBManager.getConnection()) {
            loadHistoryFromDb(conn);
            loadFavoritesFromDb(conn);
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
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO favorites(location, latitude, longitude, added_at) VALUES (?,?,?,CURRENT_TIMESTAMP) " +
                                "ON CONFLICT(location) DO NOTHING")) {
                    ps.setString(1, location.getLocationName());
                    ps.setDouble(2, location.getLatitude());
                    ps.setDouble(3, location.getLongitude());
                    ps.executeUpdate();
                }
                loadFavoritesFromDb(conn);
                return;
            } catch (SQLException e) {
                System.err.println("DB error addToFavorites: " + e.getMessage());
                dbAvailable = false;
            }
        }
        if (!isFavorite(location.getLocationName())) {
            cacheFavorites.add(location);
        }
    }

    public void removeFromFavorites(String locationName) {
        if (locationName == null) return;
        if (dbAvailable) {
            try (Connection conn = DBManager.getConnection(); PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM favorites WHERE location = ?")) {
                ps.setString(1, locationName);
                ps.executeUpdate();
                loadFavoritesFromDb(conn);
                return;
            } catch (SQLException e) {
                System.err.println("DB error removeFromFavorites: " + e.getMessage());
                dbAvailable = false;
            }
        }
        cacheFavorites.removeIf(loc -> loc.getLocationName().equals(locationName));
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

    private void loadFavoritesFromDb(Connection conn) throws SQLException {
        cacheFavorites.clear();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT location, latitude, longitude FROM favorites ORDER BY added_at DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cacheFavorites.add(new LocationData(
                            rs.getString("location"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude")
                    ));
                }
            }
        }
        System.out.println("Loaded " + cacheFavorites.size() + " favorites");
    }

    private void pruneHistory(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY last_access DESC LIMIT " + MAX_HISTORY + ")");
        }
    }
}
