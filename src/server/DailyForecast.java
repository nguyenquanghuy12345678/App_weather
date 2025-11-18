package server;

import java.io.Serializable;

public class DailyForecast implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String date;
    private double maxTemp;
    private double minTemp;
    private int weatherCode;
    private String condition;
    private double precipitation;
    private double maxWindSpeed;
    private String sunrise;
    private String sunset;
    
    public DailyForecast() {
    }
    
    public DailyForecast(String date, double maxTemp, double minTemp, int weatherCode, 
                        String condition, double precipitation, double maxWindSpeed,
                        String sunrise, String sunset) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weatherCode = weatherCode;
        this.condition = condition;
        this.precipitation = precipitation;
        this.maxWindSpeed = maxWindSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
    
    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }
    
    public double getMinTemp() { return minTemp; }
    public void setMinTemp(double minTemp) { this.minTemp = minTemp; }
    
    public int getWeatherCode() { return weatherCode; }
    public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public double getPrecipitation() { return precipitation; }
    public void setPrecipitation(double precipitation) { this.precipitation = precipitation; }
    
    public double getMaxWindSpeed() { return maxWindSpeed; }
    public void setMaxWindSpeed(double maxWindSpeed) { this.maxWindSpeed = maxWindSpeed; }
    
    public String getSunrise() { return sunrise; }
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }
    
    public String getSunset() { return sunset; }
    public void setSunset(String sunset) { this.sunset = sunset; }
}
