package client;

import shared.LocationData;
import java.io.*;
import java.util.*;

public class SearchHistoryManager {
    private static final String HISTORY_FILE = "weather_history.dat";
    private static final String FAVORITES_FILE = "weather_favorites.dat";
    private static final int MAX_HISTORY = 20;
    
    private List<LocationData> searchHistory;
    private List<LocationData> favorites;
    
    public SearchHistoryManager() {
        searchHistory = new ArrayList<>();
        favorites = new ArrayList<>();
        loadHistory();
        loadFavorites();
    }
    
    // Search History methods
    public void addToHistory(LocationData location) {
        if (location == null) return;
        
        // Remove if already exists
        searchHistory.removeIf(loc -> loc.getLocationName().equals(location.getLocationName()));
        
        // Add to beginning
        searchHistory.add(0, location);
        
        // Keep only MAX_HISTORY items
        if (searchHistory.size() > MAX_HISTORY) {
            searchHistory = searchHistory.subList(0, MAX_HISTORY);
        }
        
        saveHistory();
    }
    
    public List<LocationData> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }
    
    public void clearHistory() {
        searchHistory.clear();
        saveHistory();
    }
    
    // Favorites methods
    public void addToFavorites(LocationData location) {
        if (location == null) return;
        
        // Check if already in favorites
        if (!isFavorite(location.getLocationName())) {
            favorites.add(location);
            saveFavorites();
        }
    }
    
    public void removeFromFavorites(String locationName) {
        favorites.removeIf(loc -> loc.getLocationName().equals(locationName));
        saveFavorites();
    }
    
    public boolean isFavorite(String locationName) {
        return favorites.stream().anyMatch(loc -> loc.getLocationName().equals(locationName));
    }
    
    public List<LocationData> getFavorites() {
        return new ArrayList<>(favorites);
    }
    
    public void clearFavorites() {
        favorites.clear();
        saveFavorites();
    }
    
    // Persistence methods
    @SuppressWarnings("unchecked")
    private void loadHistory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            searchHistory = (List<LocationData>) ois.readObject();
            System.out.println("Loaded " + searchHistory.size() + " history items");
        } catch (FileNotFoundException e) {
            System.out.println("No history file found, starting fresh");
        } catch (Exception e) {
            System.err.println("Error loading history: " + e.getMessage());
            searchHistory = new ArrayList<>();
        }
    }
    
    private void saveHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(searchHistory);
        } catch (Exception e) {
            System.err.println("Error saving history: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadFavorites() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FAVORITES_FILE))) {
            favorites = (List<LocationData>) ois.readObject();
            System.out.println("Loaded " + favorites.size() + " favorites");
        } catch (FileNotFoundException e) {
            System.out.println("No favorites file found, starting fresh");
        } catch (Exception e) {
            System.err.println("Error loading favorites: " + e.getMessage());
            favorites = new ArrayList<>();
        }
    }
    
    private void saveFavorites() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FAVORITES_FILE))) {
            oos.writeObject(favorites);
        } catch (Exception e) {
            System.err.println("Error saving favorites: " + e.getMessage());
        }
    }
}
