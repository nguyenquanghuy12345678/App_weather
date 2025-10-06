package server;

import shared.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private String clientAddress;
    private WeatherServer server;
    private boolean running = true;
    
    public ClientHandler(Socket socket, WeatherServer server) {
        this.socket = socket;
        this.server = server;
        this.clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (Exception e) {
            server.log("Client disconnected: " + clientAddress);
        } finally {
            disconnect();
        }
    }
    
    private void handleMessage(Message message) {
        switch (message.getType()) {
            case Constants.MSG_LOGIN:
                username = message.getUsername();
                server.log("User logged in: " + username + " from " + clientAddress);
                sendMessage(new Message(Constants.MSG_SUCCESS, "Login successful"));
                break;
                
            case Constants.MSG_WEATHER_REQUEST:
                WeatherData weather = new WeatherData();
                Message response = new Message(Constants.MSG_WEATHER_RESPONSE, username, weather);
                sendMessage(response);
                server.log("Weather data sent to: " + username);
                break;
                
            case Constants.MSG_LOGOUT:
                running = false;
                break;
        }
    }
    
    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            running = false;
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            server.removeClient(clientAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getUsername() { return username; }
    public String getClientAddress() { return clientAddress; }
    public String getConnectedTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}