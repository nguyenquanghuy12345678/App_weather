package shared;

import java.io.Serializable;

public class LocationData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String locationName;
    private double latitude;
    private double longitude;
    
    public LocationData() {
    }
    
    public LocationData(String locationName, double latitude, double longitude) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    @Override
    public String toString() {
        return locationName + " (" + latitude + ", " + longitude + ")";
    }
}
