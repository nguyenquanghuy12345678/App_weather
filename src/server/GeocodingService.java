package server;

import shared.LocationData;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeocodingService {
    
    /**
     * Geocode a location name to coordinates using Open-Meteo Geocoding API
     * @param locationName The name of the location to search
     * @return LocationData with coordinates, or null if not found
     */
    public static LocationData geocodeLocation(String locationName) {
        try {
            // URL encode the location name
            String encodedLocation = URLEncoder.encode(locationName, StandardCharsets.UTF_8.toString());
            
            // Open-Meteo Geocoding API URL
            String apiUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedLocation + "&count=1&language=en&format=json";
            
            System.out.println("Geocoding: " + locationName);
            System.out.println("API URL: " + apiUrl);
            
            URI uri = new URI(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "WeatherApp/1.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            int responseCode = conn.getResponseCode();
            System.out.println("Geocoding API Response Code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                
                in.close();
                conn.disconnect();
                
                String jsonResponse = content.toString();
                System.out.println("Geocoding Response: " + jsonResponse);
                
                // Parse the JSON response
                // Format: {"results":[{"name":"City","latitude":xx.xx,"longitude":yy.yy,"country":"Country"}]}
                
                // Check if results exist
                if (!jsonResponse.contains("\"results\"")) {
                    System.err.println("No results found for: " + locationName);
                    return null;
                }
                
                // Extract first result
                double latitude = parseJsonDouble(jsonResponse, "latitude");
                double longitude = parseJsonDouble(jsonResponse, "longitude");
                String name = parseJsonString(jsonResponse, "name");
                String country = parseJsonString(jsonResponse, "country");
                
                if (latitude == 0.0 && longitude == 0.0) {
                    System.err.println("Could not parse coordinates");
                    return null;
                }
                
                String fullName = name + (country != null && !country.isEmpty() ? ", " + country : "");
                
                System.out.println("Found location: " + fullName + " (" + latitude + ", " + longitude + ")");
                
                return new LocationData(fullName, latitude, longitude);
                
            } else {
                System.err.println("Geocoding API returned code: " + responseCode);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error geocoding location: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static double parseJsonDouble(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return 0.0;
            
            startIndex += searchKey.length();
            int endIndex = startIndex;
            
            // Skip whitespace
            while (endIndex < json.length() && Character.isWhitespace(json.charAt(endIndex))) {
                endIndex++;
            }
            startIndex = endIndex;
            
            // Find the end of the number
            while (endIndex < json.length()) {
                char c = json.charAt(endIndex);
                if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                    break;
                }
                endIndex++;
            }
            
            String value = json.substring(startIndex, endIndex).trim();
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private static String parseJsonString(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return "";
            
            startIndex += searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            
            if (endIndex == -1) return "";
            
            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "";
        }
    }
}
