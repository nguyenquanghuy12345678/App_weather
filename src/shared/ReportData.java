package shared;

import java.io.Serializable;

public class ReportData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String location;
    private int accuracy;
    private String comment;
    private String username;
    private String timestamp; // ISO format string from database
    
    public ReportData(String location, int accuracy, String comment, String username, String timestamp) {
        this.location = location;
        this.accuracy = accuracy;
        this.comment = comment;
        this.username = username;
        this.timestamp = timestamp;
    }
    
    // Backward compatibility constructor (without timestamp)
    public ReportData(String location, int accuracy, String comment, String username) {
        this(location, accuracy, comment, username, null);
    }
    
    public String getLocation() {
        return location;
    }
    
    public int getAccuracy() {
        return accuracy;
    }
    
    public String getComment() {
        return comment;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
}
