# Client-Server Database Synchronization Guide

## Overview
This guide explains the new client-server database synchronization feature that allows multiple clients to share favorites and community reports through the central server.

## Architecture Changes

### Previous Design (Problem)
- **Client**: Each client had its own local `weather.db` database
- **Server**: Server had its own separate `weather.db` database
- **Issue**: When client added favorites or reports, they were saved to CLIENT's local database only
- **Result**: Server's database remained empty, no data synchronization between machines

### New Design (Solution)
- **Client**: Uses in-memory cache for display, sends all database operations to server
- **Server**: Central authority for all database operations, persists to its `weather.db`
- **Synchronization**: On connection, client requests all favorites and reports from server
- **Result**: All clients share the same data through the central server

## Implementation Details

### New Message Protocol
Added 5 new message types in `shared/Constants.java`:
```java
MSG_ADD_FAVORITE       // Client → Server: Add a favorite location
MSG_REMOVE_FAVORITE    // Client → Server: Remove a favorite location
MSG_GET_FAVORITES      // Client → Server: Request all favorites
MSG_ADD_REPORT        // Client → Server: Add a community report
MSG_GET_REPORTS       // Client → Server: Request community reports
```

### New Data Transfer Objects
- **`shared/FavoriteData.java`**: Serializable class for favorite locations
  - Fields: location, latitude, longitude
  
- **`shared/ReportData.java`**: Serializable class for community reports
  - Fields: location, accuracy, comment, username

### Server-Side Changes (`server/ClientHandler.java`)
Added database operation handlers:
- `addFavoriteToDb()`: INSERT OR REPLACE into favorites table
- `removeFavoriteFromDb()`: DELETE from favorites table
- `getFavoritesFromDb()`: SELECT all favorites, returns List<LocationData>
- `addReportToDb()`: INSERT into community_reports table
- `getReportsFromDb()`: SELECT reports with optional location filter

Server responds with `MSG_SUCCESS` messages containing the data lists.

### Client-Side Changes

#### `client/SearchHistoryManager.java`
- Added `ServerCallback` interface for favorite operations
- `addToFavorites()`: Sends to server via callback instead of local DB
- `removeFromFavorites()`: Sends to server via callback instead of local DB
- `setFavorites()`: Updates local cache from server response
- Local cache maintains favorites for display purposes only

#### `client/CommunityReportsManager.java`
- Added `ServerCallback` interface for report operations
- `addReport()`: Sends to server via callback instead of local DB
- `setReports()`: Updates local cache from server response
- Local cache maintains reports for display purposes only

#### `client/CommunityReportsPanel.java`
- Added `setServerCallback()`: Connects to WeatherClient's callback implementation
- Added `getReportsManager()`: Provides access to the reports manager
- Username field now uses logged-in username instead of system username

#### `client/WeatherClient.java`
- Implemented `ServerCallback` for `SearchHistoryManager` (favorites)
  - `sendAddFavorite()`: Sends MSG_ADD_FAVORITE with FavoriteData
  - `sendRemoveFavorite()`: Sends MSG_REMOVE_FAVORITE with location name
  
- Implemented `ServerCallback` for `CommunityReportsManager` (reports)
  - `sendAddReport()`: Sends MSG_ADD_REPORT with ReportData
  - `requestReports()`: Sends MSG_GET_REPORTS with optional location filter

- Added `requestFavoritesFromServer()`: Requests favorites on connection
- Updated `handleMessage()`: Processes MSG_SUCCESS responses containing:
  - `List<LocationData>`: Favorites list → calls `historyManager.setFavorites()`
  - `List<ReportData>`: Reports list → converts to WeatherReport and calls `setReports()`

## Data Flow

### Adding a Favorite
```
1. User clicks "Add to Favorites" button
2. WeatherClient.addToFavorites() called
3. SearchHistoryManager.addToFavorites() called
4. ServerCallback.sendAddFavorite() sends MSG_ADD_FAVORITE to server
5. Server receives message in ClientHandler.handleMessage()
6. Server calls addFavoriteToDb() → INSERT OR REPLACE into favorites table
7. Server sends MSG_SUCCESS response
8. Client receives response (optional: refresh favorites list)
```

### Loading Favorites on Connection
```
1. Client connects and logs in successfully
2. WeatherClient.connectToServer() calls requestFavoritesFromServer()
3. Client sends MSG_GET_FAVORITES to server
4. Server calls getFavoritesFromDb() → SELECT all from favorites table
5. Server sends MSG_SUCCESS with List<LocationData>
6. Client handleMessage() detects List<LocationData>
7. Client calls historyManager.setFavorites() to update local cache
8. UI displays favorites from cache
```

### Adding a Community Report
```
1. User submits a report with accuracy rating and comment
2. CommunityReportsPanel.submitReport() called
3. ServerCallback.sendAddReport() sends MSG_ADD_REPORT to server
4. Server receives message in ClientHandler.handleMessage()
5. Server calls addReportToDb() → INSERT into community_reports table
6. Server sends MSG_SUCCESS response
7. Client receives response
```

### Loading Community Reports
```
1. User clicks to view reports for a location
2. ServerCallback.requestReports() sends MSG_GET_REPORTS to server
3. Server calls getReportsFromDb() with location filter
4. Server sends MSG_SUCCESS with List<ReportData>
5. Client handleMessage() detects List<ReportData>
6. Client converts to WeatherReport objects and calls setReports()
7. UI displays reports from cache
```

## Testing Multi-Machine Setup

### Prerequisites
- Two computers on the same network (or use localhost for testing)
- Compiled Weather application on both machines
- SQLite JDBC driver (lib\sqlite-jdbc-3.50.3.0.jar) on both machines

### Test Scenario 1: Multi-Machine Setup

#### Machine 1 (Server Machine)
```batch
# Run the server
cd D:\eclipse-workspace\App_weather
.\run_server.bat

# Server should start and display:
# WeatherServer started on port 8889
```

#### Machine 2 (Client Machine)
```batch
# Edit run_client.bat to point to Server Machine's IP
# Change: set SERVER_HOST=localhost
# To: set SERVER_HOST=192.168.1.100  (replace with actual server IP)

# Run the client
cd D:\eclipse-workspace\App_weather
.\run_client.bat

# Client should connect to remote server
```

#### Verification Steps
1. **Login** on Machine 2 client with username "user1"
2. **Search** for a location (e.g., "Hanoi")
3. **Add to Favorites** - click the favorite button
4. **Check Server Database** on Machine 1:
   ```batch
   # Open SQLite command line or DB browser
   sqlite3 weather.db
   SELECT * FROM favorites;
   # Should show: Hanoi with coordinates
   ```
5. **Submit Community Report** with 5-star rating and comment "Very accurate!"
6. **Check Server Database** on Machine 1:
   ```sql
   SELECT * FROM community_reports;
   # Should show: Hanoi, 5, "Very accurate!", user1
   ```
7. **Start Second Client** on Machine 2 or another machine
8. **Login** with different username "user2"
9. **Verify** that favorites list shows Hanoi (from user1)
10. **View Community Reports** - should see user1's report

### Test Scenario 2: Localhost Testing (Single Machine)
```batch
# Terminal 1: Start Server
cd D:\eclipse-workspace\App_weather
.\run_server.bat

# Terminal 2: Start Client 1
cd D:\eclipse-workspace\App_weather
.\run_client.bat

# Terminal 3: Start Client 2
cd D:\eclipse-workspace\App_weather
.\run_client.bat
```

Follow same verification steps as above, but both clients on same machine.

### Expected Results
- ✅ Favorites added by any client appear in server's weather.db
- ✅ Favorites are visible to all connected clients
- ✅ Community reports added by any client appear in server's weather.db
- ✅ Community reports are visible to all connected clients
- ✅ Server database (weather.db) is the single source of truth
- ✅ Client databases (if any) are not used for favorites/reports

### Troubleshooting

#### Problem: Client can't connect to server
- Check firewall settings on server machine (allow port 8889)
- Verify server is running: `netstat -an | findstr 8889`
- Check client can ping server: `ping <server-ip>`

#### Problem: Favorites not appearing in server database
- Check server console for "Favorite added by..." log messages
- Verify server's weather.db file exists and is writable
- Check client console for any exception stack traces

#### Problem: Old local database still being used
- Delete client's weather.db file (should not be needed)
- Client should only use in-memory cache, not local database

## Database Schema

### favorites table
```sql
CREATE TABLE IF NOT EXISTS favorites (
    location TEXT PRIMARY KEY,
    latitude REAL,
    longitude REAL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

### community_reports table
```sql
CREATE TABLE IF NOT EXISTS community_reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    location TEXT,
    accuracy INTEGER,
    comment TEXT,
    username TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

## Migration Notes

### From Previous Version
If upgrading from the old version where clients had local databases:
1. Client local databases will NOT be automatically migrated
2. Users will need to re-add their favorites on the new system
3. Old community reports in client databases will not be visible
4. Server database starts fresh (or use existing server database)

### Clean Installation
1. Compile project: `.\build.bat`
2. Start server: `.\run_server.bat`
3. Server automatically creates weather.db with correct schema
4. Start clients: `.\run_client.bat`
5. Clients connect and sync data from server

## Future Enhancements
- [ ] Real-time updates: Push notifications when other clients add data
- [ ] Conflict resolution: Handle concurrent updates gracefully
- [ ] Offline mode: Queue operations when disconnected, sync on reconnect
- [ ] User-specific favorites: Each user has their own favorites (currently global)
- [ ] Report moderation: Admin interface to manage community reports
- [ ] Database backup: Automated backup of server weather.db
