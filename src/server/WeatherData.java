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
    
    // Extended weather data
    private double pressure; // hPa
    private double uvIndex;
    private double visibility; // km
    private String sunrise;
    private String sunset;
    private int cloudCover; // %
    private double precipitation; // mm
    private String windDirection;
    private double feelsLike; // °C
    
    // Forecast data (7 days)
    private java.util.List<DailyForecast> forecast;
    
    // Default coordinates for Da Nang, Vietnam
    private static final double DEFAULT_LATITUDE = 16.0544;
    private static final double DEFAULT_LONGITUDE = 108.2022;
    
    public WeatherData() {
        this.location = "Da Nang, Vietnam";
        this.forecast = new java.util.ArrayList<>();
        fetchWeatherFromAPI(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    }
    
    public WeatherData(String location, double latitude, double longitude) {
        this.location = location;
        this.forecast = new java.util.ArrayList<>();
        fetchWeatherFromAPI(latitude, longitude);
    }
    
    private void fetchWeatherFromAPI(double latitude, double longitude) {
        try {
            // Open-Meteo API URL with extended parameters
            String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?" +
                "latitude=%.4f&longitude=%.4f" +
                "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m," +
                "surface_pressure,cloud_cover,wind_direction_10m,apparent_temperature" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min," +
                "sunrise,sunset,precipitation_sum,wind_speed_10m_max,uv_index_max" +
                "&timezone=auto" +
                "&forecast_days=7",
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
                this.pressure = parseJsonDouble(jsonResponse, "surface_pressure");
                this.cloudCover = (int) parseJsonDouble(jsonResponse, "cloud_cover");
                this.feelsLike = parseJsonDouble(jsonResponse, "apparent_temperature");
                
                double windDir = parseJsonDouble(jsonResponse, "wind_direction_10m");
                this.windDirection = convertWindDirection(windDir);
                
                // Map weather code to condition
                int weatherCode = (int) parseJsonDouble(jsonResponse, "weather_code");
                this.condition = mapWeatherCode(weatherCode);
                
                // Parse daily forecast data
                parseForecastData(jsonResponse);
                
                this.lastUpdate = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                
                System.out.println("Weather data fetched successfully!");
                System.out.println("Temperature: " + this.temperature + "°C");
                System.out.println("Humidity: " + this.humidity + "%");
                System.out.println("Wind Speed: " + this.windSpeed + " km/h");
                System.out.println("Pressure: " + this.pressure + " hPa");
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
    
    private String convertWindDirection(double degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                              "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(degrees / 22.5) % 16;
        return directions[index];
    }
    
    private void parseForecastData(String json) {
        try {
            forecast.clear();
            
            // Find the daily object
            String dailyMarker = "\"daily\":{";
            int dailyStart = json.indexOf(dailyMarker);
            if (dailyStart == -1) return;
            
            // Parse arrays from daily object
            String[] dates = parseJsonArray(json, "time", dailyStart);
            double[] maxTemps = parseJsonDoubleArray(json, "temperature_2m_max", dailyStart);
            double[] minTemps = parseJsonDoubleArray(json, "temperature_2m_min", dailyStart);
            int[] weatherCodes = parseJsonIntArray(json, "weather_code", dailyStart);
            double[] precipitation = parseJsonDoubleArray(json, "precipitation_sum", dailyStart);
            double[] windSpeeds = parseJsonDoubleArray(json, "wind_speed_10m_max", dailyStart);
            String[] sunrises = parseJsonArray(json, "sunrise", dailyStart);
            String[] sunsets = parseJsonArray(json, "sunset", dailyStart);
            double[] uvIndexes = parseJsonDoubleArray(json, "uv_index_max", dailyStart);
            
            // Use the first day's data for current weather extended info
            if (uvIndexes != null && uvIndexes.length > 0) {
                this.uvIndex = uvIndexes[0];
            }
            if (sunrises != null && sunrises.length > 0) {
                this.sunrise = formatTime(sunrises[0]);
            }
            if (sunsets != null && sunsets.length > 0) {
                this.sunset = formatTime(sunsets[0]);
            }
            if (precipitation != null && precipitation.length > 0) {
                this.precipitation = precipitation[0];
            }
            
            // Create forecast entries
            int days = Math.min(7, dates != null ? dates.length : 0);
            for (int i = 0; i < days; i++) {
                DailyForecast day = new DailyForecast();
                day.setDate(dates[i]);
                day.setMaxTemp(maxTemps[i]);
                day.setMinTemp(minTemps[i]);
                day.setWeatherCode(weatherCodes[i]);
                day.setCondition(mapWeatherCode(weatherCodes[i]));
                day.setPrecipitation(precipitation[i]);
                day.setMaxWindSpeed(windSpeeds[i]);
                day.setSunrise(formatTime(sunrises[i]));
                day.setSunset(formatTime(sunsets[i]));
                forecast.add(day);
            }
            
            System.out.println("Parsed " + forecast.size() + " days of forecast data");
            
        } catch (Exception e) {
            System.err.println("Error parsing forecast data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String[] parseJsonArray(String json, String key, int startPos) {
        try {
            String searchKey = "\"" + key + "\":[";
            int startIndex = json.indexOf(searchKey, startPos);
            if (startIndex == -1) return new String[0];
            
            startIndex += searchKey.length();
            int endIndex = json.indexOf("]", startIndex);
            String arrayContent = json.substring(startIndex, endIndex);
            
            // Split by comma and clean quotes
            String[] items = arrayContent.split(",");
            for (int i = 0; i < items.length; i++) {
                items[i] = items[i].trim().replace("\"", "");
            }
            return items;
        } catch (Exception e) {
            System.err.println("Error parsing array " + key + ": " + e.getMessage());
            return new String[0];
        }
    }
    
    private double[] parseJsonDoubleArray(String json, String key, int startPos) {
        try {
            String searchKey = "\"" + key + "\":[";
            int startIndex = json.indexOf(searchKey, startPos);
            if (startIndex == -1) return new double[0];
            
            startIndex += searchKey.length();
            int endIndex = json.indexOf("]", startIndex);
            String arrayContent = json.substring(startIndex, endIndex);
            
            String[] items = arrayContent.split(",");
            double[] result = new double[items.length];
            for (int i = 0; i < items.length; i++) {
                result[i] = Double.parseDouble(items[i].trim());
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing double array " + key + ": " + e.getMessage());
            return new double[0];
        }
    }
    
    private int[] parseJsonIntArray(String json, String key, int startPos) {
        try {
            String searchKey = "\"" + key + "\":[";
            int startIndex = json.indexOf(searchKey, startPos);
            if (startIndex == -1) return new int[0];
            
            startIndex += searchKey.length();
            int endIndex = json.indexOf("]", startIndex);
            String arrayContent = json.substring(startIndex, endIndex);
            
            String[] items = arrayContent.split(",");
            int[] result = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                result[i] = Integer.parseInt(items[i].trim());
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing int array " + key + ": " + e.getMessage());
            return new int[0];
        }
    }
    
    private String formatTime(String isoTime) {
        try {
            // Convert from ISO format like "2024-01-15T06:30" to "06:30"
            if (isoTime != null && isoTime.contains("T")) {
                return isoTime.substring(isoTime.indexOf("T") + 1);
            }
            return isoTime;
        } catch (Exception e) {
            return isoTime;
        }
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
    
    public double getPressure() { return pressure; }
    public void setPressure(double pressure) { this.pressure = pressure; }
    
    public double getUvIndex() { return uvIndex; }
    public void setUvIndex(double uvIndex) { this.uvIndex = uvIndex; }
    
    public double getVisibility() { return visibility; }
    public void setVisibility(double visibility) { this.visibility = visibility; }
    
    public String getSunrise() { return sunrise; }
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }
    
    public String getSunset() { return sunset; }
    public void setSunset(String sunset) { this.sunset = sunset; }
    
    public int getCloudCover() { return cloudCover; }
    public void setCloudCover(int cloudCover) { this.cloudCover = cloudCover; }
    
    public double getPrecipitation() { return precipitation; }
    public void setPrecipitation(double precipitation) { this.precipitation = precipitation; }
    
    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
    
    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
    
    public java.util.List<DailyForecast> getForecast() { return forecast; }
    public void setForecast(java.util.List<DailyForecast> forecast) { this.forecast = forecast; }
}