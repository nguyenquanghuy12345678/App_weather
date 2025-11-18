# Weather App - Advanced Features Guide

## 🌟 Tính năng mới đã được thêm vào

### 1. 📊 Thông tin thời tiết chi tiết
- **Nhiệt độ cảm nhận** (Feels Like)
- **Áp suất khí quyển** (hPa)
- **Chỉ số UV** (UV Index)
- **Độ che phủ mây** (Cloud Cover %)
- **Lượng mưa** (Precipitation mm)
- **Hướng gió** (Wind Direction - N, NE, E, SE, S, SW, W, NW)
- **Giờ mặt trời mọc/lặn** (Sunrise/Sunset)

### 2. 📅 Dự báo 7 ngày
- Hiển thị dự báo thời tiết cho 7 ngày tới
- **Biểu đồ nhiệt độ** với đường Max/Min temperature
- Thông tin chi tiết mỗi ngày:
  - Nhiệt độ cao nhất/thấp nhất
  - Điều kiện thời tiết
  - Lượng mưa dự kiến
  - Tốc độ gió tối đa
  - Giờ mặt trời mọc/lặn

### 3. 🗺️ Bản đồ tương tác
- Hiển thị vị trí địa điểm trên bản đồ
- Tọa độ chính xác (Latitude/Longitude)
- Nút **"Open in Browser"** để xem bản đồ tương tác với:
  - **OpenStreetMap** (miễn phí, không cần API key)
  - **Leaflet.js** cho interactive map
  - Marker hiển thị vị trí
  - Popup với thông tin địa điểm

### 4. ⭐ Yêu thích & Lịch sử
- **Search History Manager**:
  - Tự động lưu 20 địa điểm tìm kiếm gần nhất
  - Lưu vào file `weather_history.dat`
- **Favorites**:
  - Đánh dấu địa điểm yêu thích
  - Lưu vào file `weather_favorites.dat`
  - Nút ⭐ Favorite trên toolbar

### 5. 📤 Chia sẻ thông tin thời tiết
**Share Dialog** với các tùy chọn:
- **📋 Copy to Clipboard**: Copy text vào clipboard
- **💾 Save as Text**: Lưu báo cáo dạng .txt
- **🖼️ Save as Image**: Tạo và lưu hình ảnh PNG với:
  - Background gradient đẹp mắt
  - Hiển thị đầy đủ thông tin
  - Định dạng chuyên nghiệp

### 6. 👥 Community Weather Reports
- **Đánh giá độ chính xác** của dự báo (1-5 sao)
- **Nhận xét** về thời tiết thực tế
- Lưu trữ và hiển thị báo cáo từ cộng đồng
- Lọc theo địa điểm
- Lưu vào file `community_reports.dat`

### 7. 🎨 Giao diện đa tab
**5 tabs chính:**
1. **🌤 Current** - Thời tiết hiện tại (giao diện gốc)
2. **📊 Details** - Thông tin chi tiết đầy đủ
3. **📅 Forecast** - Dự báo 7 ngày + biểu đồ
4. **🗺️ Map** - Bản đồ vị trí
5. **👥 Community** - Báo cáo cộng đồng

## 🚀 Cách sử dụng

### Khởi động Server
```bash
# Compile
javac -d bin -sourcepath src src/server/WeatherServer.java

# Run
java -cp bin server.WeatherServer
```
1. Nhập Port (mặc định: 8888)
2. Click **Start Server**
3. Server sẽ lắng nghe kết nối từ client

### Khởi động Client
```bash
# Compile
javac -d bin -sourcepath src src/client/WeatherClient.java

# Run
java -cp bin client.WeatherClient
```
1. **Login Dialog** xuất hiện
2. Nhập:
   - Host: localhost (hoặc IP của server)
   - Port: 8888
   - Username: tên của bạn
3. Click **Connect**

### Sử dụng các tính năng

#### Tìm kiếm địa điểm
1. Nhập tên thành phố vào ô tìm kiếm
2. Click **Tìm kiếm** hoặc Enter
3. Thông tin thời tiết sẽ hiển thị trên tất cả các tab

#### Xem dự báo 7 ngày
1. Chuyển sang tab **📅 Forecast**
2. Xem biểu đồ nhiệt độ
3. Xem chi tiết từng ngày ở dưới

#### Xem bản đồ
1. Chuyển sang tab **🗺️ Map**
2. Click **🗺️ Open in Browser** để xem bản đồ tương tác
3. Bản đồ sẽ mở trong trình duyệt với OpenStreetMap

#### Đánh dấu yêu thích
1. Tìm kiếm một địa điểm
2. Click nút **⭐ Favorite** trên toolbar
3. Địa điểm được lưu vào favorites

#### Chia sẻ thông tin
1. Click nút **📤 Share** trên toolbar
2. Chọn cách chia sẻ:
   - Copy text
   - Lưu file text
   - Lưu hình ảnh PNG

#### Gửi báo cáo cộng đồng
1. Chuyển sang tab **👥 Community**
2. Chọn mức độ chính xác (1-5 sao)
3. Nhập nhận xét (tùy chọn)
4. Click **📤 Submit Report**

## 🔧 API sử dụng

### Open-Meteo API (MIỄN PHÍ)
- **URL**: https://api.open-meteo.com/v1/forecast
- **Không cần API Key**
- **Dữ liệu lấy được**:
  - Current weather: nhiệt độ, độ ẩm, tốc độ gió, áp suất, mây che phủ
  - Daily forecast: 7 ngày dự báo
  - Sunrise/Sunset times
  - UV Index
  - Weather codes (mã thời tiết)

### OpenStreetMap (MIỄN PHÍ)
- **Tiles**: https://tile.openstreetmap.org
- **Leaflet.js**: https://unpkg.com/leaflet@1.9.4
- **Không cần API Key**
- Interactive map với zoom, pan, markers

## 📁 Cấu trúc Project mới

```
App_weather/
├── src/
│   ├── client/
│   │   ├── WeatherClient.java        (Cập nhật: tabs, buttons mới)
│   │   ├── WeatherPanel.java         (Giao diện gốc)
│   │   ├── DetailedWeatherPanel.java (MỚI: Thông tin chi tiết)
│   │   ├── ForecastPanel.java        (MỚI: Dự báo 7 ngày + chart)
│   │   ├── MapPanel.java             (MỚI: Bản đồ OpenStreetMap)
│   │   ├── ShareDialog.java          (MỚI: Chia sẻ thông tin)
│   │   ├── CommunityReportsPanel.java (MỚI: Báo cáo cộng đồng)
│   │   ├── SearchHistoryManager.java (MỚI: Quản lý lịch sử)
│   │   └── ...
│   ├── server/
│   │   ├── WeatherData.java          (Cập nhật: thêm nhiều field)
│   │   ├── DailyForecast.java        (MỚI: Dữ liệu dự báo hàng ngày)
│   │   └── ...
│   └── shared/
│       └── ...
├── weather_history.dat        (Auto-generated: Lịch sử tìm kiếm)
├── weather_favorites.dat      (Auto-generated: Địa điểm yêu thích)
├── community_reports.dat      (Auto-generated: Báo cáo cộng đồng)
└── README_ADVANCED_FEATURES.md (File này)
```

## 🎯 Điểm nổi bật

### ✅ Hoàn toàn miễn phí
- Không cần API key
- Không giới hạn số lượng request
- Không cần đăng ký tài khoản

### ✅ Offline-capable
- Lịch sử và favorites lưu local
- Community reports lưu local
- Không cần internet để xem dữ liệu đã lưu

### ✅ User-friendly
- Giao diện đẹp, hiện đại
- Nhiều tab phân loại rõ ràng
- Icons trực quan
- Màu sắc hài hòa

### ✅ Feature-rich
- Đầy đủ thông tin thời tiết
- Dự báo 7 ngày với biểu đồ
- Bản đồ tương tác
- Chia sẻ đa dạng
- Cộng đồng đánh giá

## 🔮 Hướng phát triển tiếp theo

### Có thể thêm:
1. **Alerts/Notifications**:
   - Cảnh báo thời tiết xấu
   - Thông báo khi nhiệt độ thay đổi đột ngột

2. **Multiple Cities Dashboard**:
   - Xem nhiều thành phố cùng lúc
   - So sánh thời tiết

3. **Weather Widgets**:
   - Mini widget trên desktop
   - System tray icon

4. **Export to PDF**:
   - Xuất báo cáo PDF chuyên nghiệp
   - Bao gồm biểu đồ và bản đồ

5. **Historical Data**:
   - Xem lịch sử thời tiết
   - Biểu đồ xu hướng theo tháng/năm

6. **Multi-language**:
   - Tiếng Việt
   - English
   - Các ngôn ngữ khác

## 📝 Ghi chú kỹ thuật

### Biểu đồ nhiệt độ
- Tự vẽ bằng Graphics2D (không cần JFreeChart)
- Smooth line với anti-aliasing
- Tự động scale theo min/max value
- Hiển thị grid và labels

### Bản đồ
- Sử dụng JEditorPane hiển thị HTML
- Leaflet.js cho interactive map trong browser
- OpenStreetMap tiles (free)
- Tích hợp Google Maps link

### Persistence
- Serialization cho Java objects
- Files lưu ở thư mục hiện tại
- Tự động tạo nếu không tồn tại

## 🐛 Troubleshooting

### Map không hiển thị
- Đảm bảo có kết nối internet
- Click "Open in Browser" để xem full map

### Lỗi kết nối API
- Kiểm tra internet connection
- Open-Meteo API có thể chậm đôi khi
- Server sẽ retry tự động

### File không lưu được
- Kiểm tra quyền ghi file
- Đảm bảo đủ dung lượng ổ đĩa

---

**Phát triển bởi**: Weather App Team  
**Ngày cập nhật**: November 2025  
**Version**: 2.0 - Advanced Features
