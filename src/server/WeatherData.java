package server;

import java.io.Serializable;
import java.util.Random;

public class WeatherData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String location;
    private double temperature;
    private int humidity;
    private String condition;
    private double windSpeed;
    private String lastUpdate;
    
    public WeatherData() {
        generateRandomWeather();
    }
    
    public WeatherData(String location) {
        this.location = location;
        generateRandomWeather();
    }
    
    private void generateRandomWeather() {
        Random rand = new Random();
        this.location = "Da Nang, Vietnam";
        this.temperature = 25 + rand.nextDouble() * 15; // 25-40°C
        this.humidity = 60 + rand.nextInt(40); // 60-100%
        this.windSpeed = 5 + rand.nextDouble() * 20; // 5-25 km/h
        
        String[] conditions = {"Sunny", "Partly Cloudy", "Cloudy", "Rainy", "Stormy"};
        this.condition = conditions[rand.nextInt(conditions.length)];
        
        this.lastUpdate = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
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