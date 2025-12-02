package shared;

import java.io.Serializable;

public class ReportData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String location;
    private int accuracy;
    private String comment;
    private String username;
    
    public ReportData(String location, int accuracy, String comment, String username) {
        this.location = location;
        this.accuracy = accuracy;
        this.comment = comment;
        this.username = username;
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
}
