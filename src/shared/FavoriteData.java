package shared;

import java.io.Serializable;

public class FavoriteData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String location;
    private double latitude;
    private double longitude;
    
    public FavoriteData(String location, double latitude, double longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getLocation() {
        return location;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
}
