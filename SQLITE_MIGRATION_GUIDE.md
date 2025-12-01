# Weather App - SQLite Migration & UI Improvements

## 📋 Tổng Quan Thay Đổi

Project đã được nâng cấp hoàn toàn từ file serialization (.dat) sang SQLite database với các cải tiến về icon loading và giao diện.

---

## 🗄️ Database Schema (SQLite)

### File: `weather.db`

```sql
-- Lịch sử tìm kiếm
CREATE TABLE search_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    location TEXT UNIQUE NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Địa điểm yêu thích
CREATE TABLE favorites (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    location TEXT UNIQUE NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Báo cáo cộng đồng
CREATE TABLE community_reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    location TEXT NOT NULL,
    accuracy INTEGER NOT NULL,      -- Đánh giá 1-5 sao
    comment TEXT,
    username TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 📦 Các Manager Class

### 1. DBManager (`shared/DBManager.java`)
**Chức năng:** Quản lý kết nối SQLite
- Singleton connection pattern
- Auto-init schema khi chạy lần đầu
- Thread-safe
- Graceful error handling

```java
// Sử dụng
Connection conn = DBManager.getConnection();
```

### 2. SearchHistoryManager (`client/SearchHistoryManager.java`)
**Chức năng:** Quản lý search history & favorites
- **CRUD operations:**
  - `addToHistory(LocationData)` - Thêm vào lịch sử (UPSERT)
  - `getSearchHistory()` - Lấy 20 item gần nhất
  - `addToFavorites(LocationData)` - Thêm yêu thích
  - `removeFromFavorites(String)` - Xóa yêu thích
  - `isFavorite(String)` - Kiểm tra yêu thích
- **Features:**
  - Auto-prune history (giữ max 20)
  - In-memory fallback nếu DB không khả dụng
  - ON CONFLICT handling

### 3. CommunityReportsManager (`client/CommunityReportsManager.java`)
**Chức năng:** Quản lý community weather reports
- **Operations:**
  - `addReport(location, accuracy, comment, username)` - Thêm báo cáo
  - `getReports(location)` - Lấy báo cáo theo location
  - `getAllReports()` - Lấy tất cả
  - `getStatsForLocation(location)` - Thống kê (total, avg accuracy)
  - `clearReports()` - Xóa toàn bộ
- **Features:**
  - Persistent storage trong SQLite
  - Real-time statistics
  - Limit 1000 reports gần nhất

---

## 🎨 Icon & UI Improvements

### IconManager (`shared/IconManager.java`)

**Cải tiến:**
1. **Multi-source loading:** Classpath → Filesystem → Default fallback
2. **Condition mapping:** Auto-map weather condition sang icon name
3. **Null-safe:** Không bao giờ crash khi icon thiếu

**API:**
```java
// Load với fallback
ImageIcon icon = IconManager.loadIcon("sun.png", 64);

// Map condition
String iconName = IconManager.mapConditionToIcon("Clear sky"); // → "sun.png"

// Helper methods
JLabel label = IconManager.createIconLabel("user.png", "Username", 16);
JButton btn = IconManager.createIconButton("refresh.png", "Refresh", 16);
```

**Condition Mapping:**
| Condition | Icon |
|-----------|------|
| clear, sunny | sun.png |
| partly cloudy | partly_cloudy.png |
| cloudy | cloudy.png |
| rain, drizzle | rain.png |
| storm, thunder | storm.png |
| snow | snow.png |
| fog | fog.png |
| error, not found | error.png |
| default | default.png |

### WeatherPanel & DetailedWeatherPanel

**Fixes:**
1. **Layout overlap fix:** 
   - Problem: `BorderLayout.SOUTH` vs `BorderLayout.PAGE_END` conflict
   - Solution: Wrapper container cho main+details
2. **Icon loading:**
   - Old: Hardcoded `"resources/icons/sun.png"`
   - New: `IconManager.loadIcon("sun.png", size)`
3. **Fallback:**
   - Icon không tìm thấy → Emoji "?" hoặc "☁"

---

## 🗑️ Đã Xóa

### Files
- ❌ `weather_history.dat`
- ❌ `weather_favorites.dat`
- ❌ `community_reports.dat`

### Code
- ❌ `ObjectInputStream/ObjectOutputStream` serialization
- ❌ File I/O cho persistence
- ❌ Hardcoded icon paths

---

## 📂 Cấu Trúc Project

```
App_weather/
├── src/
│   ├── shared/
│   │   ├── DBManager.java          ← SQLite manager
│   │   ├── IconManager.java        ← Icon loading với fallback
│   │   ├── Constants.java
│   │   ├── LocationData.java
│   │   └── Message.java
│   ├── client/
│   │   ├── SearchHistoryManager.java       ← SQLite history/favorites
│   │   ├── CommunityReportsManager.java    ← SQLite reports (MỚI)
│   │   ├── CommunityReportsPanel.java      ← UI với stats
│   │   ├── WeatherPanel.java              ← Fixed layout + icons
│   │   ├── DetailedWeatherPanel.java      ← Fixed layout + icons
│   │   ├── WeatherClient.java
│   │   ├── LoginDialog.java
│   │   └── ...
│   └── server/
│       ├── WeatherServer.java
│       ├── ClientHandler.java
│       ├── WeatherData.java
│       └── ...
├── lib/
│   └── sqlite-jdbc-3.45.x.x.jar    ← JDBC driver
├── resources/
│   └── icons/                       ← Weather icons
├── weather.db                       ← SQLite database
├── build.bat
├── run_server.bat
└── run_client.bat
```

---

## 🚀 Hướng Dẫn Sử Dụng

### Build Project
```bash
build.bat
```

### Run Server
```bash
run_server.bat
```

### Run Client
```bash
run_client.bat
```

---

## ✨ Features

### 1. Search History (Persistent)
- Tự động lưu 20 địa điểm tìm kiếm gần nhất
- UPSERT: Update timestamp nếu đã tồn tại

### 2. Favorites (Persistent)
- Thêm/xóa địa điểm yêu thích
- Kiểm tra nhanh `isFavorite()`

### 3. Community Reports (Persistent)
- User submit accuracy rating (1-5⭐)
- Bình luận tùy chọn
- Thống kê real-time:
  - Tổng số reports
  - Average accuracy per location
- Filter theo location

### 4. Smart Icon Loading
- Load từ JAR (khi đóng gói)
- Load từ filesystem (development)
- Fallback sang default icon
- No broken images!

---

## 🔧 Requirements

- Java 11+ (module system)
- SQLite JDBC Driver: `sqlite-jdbc-3.45.x.x.jar`
- Requires java.sql module

---

## 📊 Database Statistics

Xem database:
```bash
# Cài SQLite browser hoặc dùng CLI
sqlite3 weather.db

# Query examples
SELECT * FROM search_history ORDER BY last_access DESC LIMIT 10;
SELECT * FROM favorites;
SELECT location, AVG(accuracy) as avg_rating 
FROM community_reports 
GROUP BY location;
```

---

## 🐛 Troubleshooting

### Lỗi: "SQLite not available"
- Kiểm tra `lib/sqlite-jdbc-*.jar` có tồn tại
- Kiểm tra `module-info.java` có `requires java.sql`
- Fallback: App vẫn chạy (in-memory mode)

### Lỗi: Icon không hiển thị
- Kiểm tra `resources/icons/` folder
- IconManager tự fallback sang default.png hoặc emoji

### Lỗi: Layout overlap
- Đã fix bằng centerContainer wrapper
- Không còn SOUTH/PAGE_END conflict

---

## 📝 Notes

- Database file: `weather.db` (tự tạo lần đầu)
- Max history: 20 items
- Max reports load: 1000 gần nhất
- Thread-safe: DBManager singleton
- Graceful degradation: DB fail → in-memory mode

---

## 🎯 Next Steps (Optional)

1. **Export/Import data:** Backup weather.db
2. **Scheduled cleanup:** Auto-delete old reports (>1 năm)
3. **Cloud sync:** Sync weather.db qua cloud
4. **Analytics:** Chart cho community ratings
5. **Search:** Full-text search trong reports

---

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra console output
2. Xem file `weather.db` có được tạo
3. Verify JDBC driver trong `lib/`
4. Check module-info.java

---

**Version:** 2.0.0  
**Last Updated:** December 2, 2025  
**Database:** SQLite 3.x  
**Java:** 11+
