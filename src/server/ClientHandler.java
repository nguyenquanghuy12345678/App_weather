package server;

import shared.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private String clientAddress;
    private WeatherServer server;
    private boolean running = true;
    
    public ClientHandler(Socket socket, WeatherServer server) {
        this.socket = socket;
        this.server = server;
        this.clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            server.log("Client disconnected: " + clientAddress);
        } finally {
            disconnect();
        }
    }
    
    private void handleMessage(Message message) {
        switch (message.getType()) {
            case Constants.MSG_LOGIN:
                username = message.getUsername();
                server.log("User logged in: " + username + " from " + clientAddress);
                sendMessage(new Message(Constants.MSG_SUCCESS, "Login successful"));
                break;
                
            case Constants.MSG_WEATHER_REQUEST:
                WeatherData weather = new WeatherData();
                Message response = new Message(Constants.MSG_WEATHER_RESPONSE, username, weather);
                sendMessage(response);
                server.log("Weather data sent to: " + username);
                break;
                
            case Constants.MSG_LOCATION_SEARCH:
                // Get location data from message
                shared.LocationData locationData = (shared.LocationData) message.getData();
                if (locationData != null) {
                    // If coordinates are 0,0, we need to geocode the location name first
                    if (locationData.getLatitude() == 0.0 && locationData.getLongitude() == 0.0) {
                        server.log("Geocoding location: " + locationData.getLocationName());
                        locationData = GeocodingService.geocodeLocation(locationData.getLocationName());
                        
                        if (locationData == null) {
                            // Geocoding failed, send error
                            WeatherData errorWeather = new WeatherData();
                            errorWeather.setLocation(message.getData() != null ? 
                                ((shared.LocationData)message.getData()).getLocationName() : "Unknown");
                            errorWeather.setCondition("Location not found");
                            errorWeather.setTemperature(0);
                            errorWeather.setHumidity(0);
                            errorWeather.setWindSpeed(0);
                            errorWeather.setLastUpdate(java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                            
                            Message errorResponse = new Message(Constants.MSG_WEATHER_RESPONSE, username, errorWeather);
                            sendMessage(errorResponse);
                            server.log("Location not found for: " + username);
                            break;
                        }
                    }
                    
                    WeatherData weatherForLocation = new WeatherData(
                        locationData.getLocationName(),
                        locationData.getLatitude(),
                        locationData.getLongitude()
                    );
                    Message locationResponse = new Message(Constants.MSG_WEATHER_RESPONSE, username, weatherForLocation);
                    sendMessage(locationResponse);
                    server.log("Weather data for " + locationData.getLocationName() + " sent to: " + username);
                }
                break;
                
            case Constants.MSG_ADD_FAVORITE:
                shared.FavoriteData favData = (shared.FavoriteData) message.getData();
                if (favData != null) {
                    addFavoriteToDb(favData);
                    sendMessage(new Message(Constants.MSG_SUCCESS, "Favorite added"));
                    server.log("Favorite added by " + username + ": " + favData.getLocation());
                }
                break;
                
            case Constants.MSG_REMOVE_FAVORITE:
                String locationToRemove = (String) message.getData();
                if (locationToRemove != null) {
                    removeFavoriteFromDb(locationToRemove);
                    sendMessage(new Message(Constants.MSG_SUCCESS, "Favorite removed"));
                    server.log("Favorite removed by " + username + ": " + locationToRemove);
                }
                break;
                
            case Constants.MSG_GET_FAVORITES:
                java.util.List<shared.LocationData> favorites = getFavoritesFromDb();
                sendMessage(new Message(Constants.MSG_SUCCESS, username, favorites));
                server.log("Favorites list sent to: " + username);
                break;
                
            case Constants.MSG_ADD_REPORT:
                shared.ReportData reportData = (shared.ReportData) message.getData();
                if (reportData != null) {
                    addReportToDb(reportData);
                    sendMessage(new Message(Constants.MSG_SUCCESS, "Report added"));
                    server.log("Report added by " + username + ": " + reportData.getLocation());
                }
                break;
                
            case Constants.MSG_GET_REPORTS:
                String locationFilter = (String) message.getData();
                java.util.List<shared.ReportData> reports = getReportsFromDb(locationFilter);
                sendMessage(new Message(Constants.MSG_SUCCESS, username, reports));
                server.log("Reports list sent to: " + username);
                break;
                
            case Constants.MSG_LOGOUT:
                running = false;
                break;
        }
    }
    
    private void addFavoriteToDb(shared.FavoriteData fav) {
        try (java.sql.Connection conn = shared.DBManager.getConnection()) {
            String sql = "INSERT OR REPLACE INTO favorites(location, latitude, longitude) VALUES (?,?,?)";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fav.getLocation());
                ps.setDouble(2, fav.getLatitude());
                ps.setDouble(3, fav.getLongitude());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException e) {
            server.log("DB error adding favorite: " + e.getMessage());
        }
    }
    
    private void removeFavoriteFromDb(String location) {
        try (java.sql.Connection conn = shared.DBManager.getConnection()) {
            String sql = "DELETE FROM favorites WHERE location = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, location);
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException e) {
            server.log("DB error removing favorite: " + e.getMessage());
        }
    }
    
    private java.util.List<shared.LocationData> getFavoritesFromDb() {
        java.util.List<shared.LocationData> result = new java.util.ArrayList<>();
        try (java.sql.Connection conn = shared.DBManager.getConnection()) {
            String sql = "SELECT location, latitude, longitude FROM favorites ORDER BY added_at DESC";
            try (java.sql.Statement st = conn.createStatement();
                 java.sql.ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    result.add(new shared.LocationData(
                        rs.getString("location"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            server.log("DB error getting favorites: " + e.getMessage());
        }
        return result;
    }
    
    private void addReportToDb(shared.ReportData report) {
        try (java.sql.Connection conn = shared.DBManager.getConnection()) {
            String sql = "INSERT INTO community_reports(location, accuracy, comment, username) VALUES (?,?,?,?)";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, report.getLocation());
                ps.setInt(2, report.getAccuracy());
                ps.setString(3, report.getComment());
                ps.setString(4, report.getUsername());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException e) {
            server.log("DB error adding report: " + e.getMessage());
        }
    }
    
    private java.util.List<shared.ReportData> getReportsFromDb(String locationFilter) {
        java.util.List<shared.ReportData> result = new java.util.ArrayList<>();
        try (java.sql.Connection conn = shared.DBManager.getConnection()) {
            String sql = locationFilter == null || locationFilter.isEmpty() ?
                "SELECT location, accuracy, comment, username, timestamp FROM community_reports ORDER BY timestamp DESC LIMIT 50" :
                "SELECT location, accuracy, comment, username, timestamp FROM community_reports WHERE location = ? ORDER BY timestamp DESC LIMIT 50";
            
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                if (locationFilter != null && !locationFilter.isEmpty()) {
                    ps.setString(1, locationFilter);
                }
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new shared.ReportData(
                            rs.getString("location"),
                            rs.getInt("accuracy"),
                            rs.getString("comment"),
                            rs.getString("username"),
                            rs.getString("timestamp") // Add timestamp from DB
                        ));
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            server.log("DB error getting reports: " + e.getMessage());
        }
        return result;
    }
    
    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            running = false;
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            server.removeClient(clientAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getUsername() { return username; }
    public String getClientAddress() { return clientAddress; }
    public String getConnectedTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}