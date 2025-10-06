package shared;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;
    private String username;
    private Object data;
    private String timestamp;
    
    public Message(String type) {
        this.type = type;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
    
    public Message(String type, String username) {
        this(type);
        this.username = username;
    }
    
    public Message(String type, String username, Object data) {
        this(type, username);
        this.data = data;
    }
    
    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return "Message{type='" + type + "', username='" + username + "', timestamp='" + timestamp + "'}";
    }
}