package server;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class WeatherData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String location;
    private double temperature;
    private int humidity;
    private String condition;
    private double windSpeed;
    private String lastUpdate;
    
    // Default coordinates for Da Nang, Vietnam
    private static final double DEFAULT_LATITUDE = 16.0544;
    private static final double DEFAULT_LONGITUDE = 108.2022;
    
    public WeatherData() {
        this.location = "Da Nang, Vietnam";
        fetchWeatherFromAPI(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    }
    
    public WeatherData(String location, double latitude, double longitude) {
        this.location = location;
        fetchWeatherFromAPI(latitude, longitude);
    }
    
    private void fetchWeatherFromAPI(double latitude, double longitude) {
        try {
            // Open-Meteo API URL
            String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Asia/Bangkok",
                latitude, longitude
            );
            
            System.out.println("Fetching weather from: " + apiUrl);
            
            URI uri = new URI(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "WeatherApp/1.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            int responseCode = conn.getResponseCode();
            System.out.println("API Response Code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                
                in.close();
                conn.disconnect();
                
                // Parse JSON response manually
                String jsonResponse = content.toString();
                System.out.println("API Response: " + jsonResponse);
                
                this.temperature = parseJsonDouble(jsonResponse, "temperature_2m");
                this.humidity = (int) parseJsonDouble(jsonResponse, "relative_humidity_2m");
                this.windSpeed = parseJsonDouble(jsonResponse, "wind_speed_10m");
                
                // Map weather code to condition
                int weatherCode = (int) parseJsonDouble(jsonResponse, "weather_code");
                this.condition = mapWeatherCode(weatherCode);
                
                this.lastUpdate = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                
                System.out.println("Weather data fetched successfully!");
                System.out.println("Temperature: " + this.temperature + "°C");
                System.out.println("Humidity: " + this.humidity + "%");
                System.out.println("Wind Speed: " + this.windSpeed + " km/h");
                System.out.println("Condition: " + this.condition);
                
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorContent = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorContent.append(errorLine);
                }
                errorReader.close();
                System.err.println("API Error Response: " + errorContent.toString());
                throw new Exception("API returned code: " + responseCode);
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
            e.printStackTrace();
            // Set default values on error
            this.temperature = 25.0;
            this.humidity = 70;
            this.windSpeed = 10.0;
            this.condition = "Data Unavailable";
            this.lastUpdate = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
    }
    
    private double parseJsonDouble(String json, String key) {
        try {
            // Look for the key in the "current" object
            String currentMarker = "\"current\":{";
            int currentStart = json.indexOf(currentMarker);
            if (currentStart == -1) {
                System.err.println("Could not find 'current' object in JSON");
                return 0.0;
            }
            
            // Search within the current object
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey, currentStart);
            if (startIndex == -1) {
                System.err.println("Could not find key '" + key + "' in JSON");
                return 0.0;
            }
            
            startIndex += searchKey.length();
            int endIndex = startIndex;
            
            // Skip whitespace
            while (endIndex < json.length() && Character.isWhitespace(json.charAt(endIndex))) {
                endIndex++;
            }
            startIndex = endIndex;
            
            // Find the end of the number (comma, closing brace, or closing bracket)
            while (endIndex < json.length()) {
                char c = json.charAt(endIndex);
                if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                    break;
                }
                endIndex++;
            }
            
            String value = json.substring(startIndex, endIndex).trim();
            System.out.println("Parsing " + key + " = " + value);
            double result = Double.parseDouble(value);
            System.out.println("Parsed successfully: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing " + key + ": " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
    
    private String mapWeatherCode(int code) {
        // Open-Meteo weather code mapping
        if (code == 0) return "Clear Sky";
        if (code == 1 || code == 2) return "Partly Cloudy";
        if (code == 3) return "Cloudy";
        if (code >= 45 && code <= 48) return "Foggy";
        if (code >= 51 && code <= 57) return "Drizzle";
        if (code >= 61 && code <= 67) return "Rainy";
        if (code >= 71 && code <= 77) return "Snowy";
        if (code >= 80 && code <= 82) return "Rain Showers";
        if (code >= 85 && code <= 86) return "Snow Showers";
        if (code >= 95 && code <= 99) return "Stormy";
        return "Unknown";
    }
    
    // Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    
    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
}