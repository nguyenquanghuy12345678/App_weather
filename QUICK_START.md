# Quick Start Guide - Hướng dẫn nhanh

## 🚀 Chạy ứng dụng

### Bước 1: Compile tất cả
```powershell
# Compile toàn bộ project
javac -d bin -sourcepath src src/server/*.java src/client/*.java src/shared/*.java
```

### Bước 2: Chạy Server
```powershell
# Mở terminal thứ nhất
java -cp bin server.WeatherServer
```
- Port mặc định: 8888
- Click "▶ Start Server"

### Bước 3: Chạy Client
```powershell
# Mở terminal thứ hai
java -cp bin client.WeatherClient
```
- Host: localhost
- Port: 8888
- Username: (nhập tên bạn)
- Click "Connect"

## 🎯 Demo các tính năng

### 1. Xem thời tiết hiện tại
- Tab **🌤 Current**
- Nhập "Hanoi" → Click "Tìm kiếm"
- Xem nhiệt độ, độ ẩm, tốc độ gió

### 2. Xem chi tiết đầy đủ
- Tab **📊 Details**
- Xem thêm: UV Index, Áp suất, Mây che phủ, Giờ mặt trời mọc/lặn

### 3. Dự báo 7 ngày
- Tab **📅 Forecast**
- Xem biểu đồ nhiệt độ Max/Min
- Xem chi tiết từng ngày

### 4. Bản đồ
- Tab **🗺️ Map**
- Click "🗺️ Open in Browser"
- Xem bản đồ tương tác với Leaflet + OpenStreetMap

### 5. Đánh dấu yêu thích
- Tìm địa điểm bất kỳ
- Click "⭐ Favorite" trên toolbar
- Địa điểm được lưu vào favorites

### 6. Chia sẻ
- Click "📤 Share" trên toolbar
- Thử các tùy chọn:
  - Copy to Clipboard
  - Save as Text
  - Save as Image (PNG)

### 7. Community Reports
- Tab **👥 Community**
- Chọn số sao (1-5)
- Nhập nhận xét
- Click "📤 Submit Report"
- Xem báo cáo của mọi người

## 📝 Test Cases

### Test 1: Nhiều địa điểm
```
1. Tìm "Tokyo, Japan"
2. Chuyển sang tab Forecast → Xem dự báo
3. Tìm "London, UK"
4. Chuyển sang tab Map → Xem vị trí
5. Tìm "New York, USA"
6. Share → Save as Image
```

### Test 2: Favorites
```
1. Tìm "Da Nang, Vietnam" → Click Favorite
2. Tìm "Seoul, South Korea" → Click Favorite
3. Tìm "Bangkok, Thailand" → Click Favorite
4. Restart client → Favorites vẫn còn
```

### Test 3: Community Reports
```
1. Tìm "Hanoi"
2. Tab Community → Rate 5 sao → "Weather is perfect!"
3. Tìm "Tokyo"
4. Tab Community → Rate 3 sao → "Little bit cloudy"
5. Quay lại "Hanoi" → Xem report đã submit
```

### Test 4: Export/Share
```
1. Tìm bất kỳ địa điểm
2. Click Share
3. Save as Image → Kiểm tra file PNG
4. Save as Text → Kiểm tra file TXT
5. Copy to Clipboard → Paste vào notepad
```

## 🔍 Kiểm tra Files

Sau khi sử dụng, check các files được tạo:
```
weather_history.dat      ← Lịch sử tìm kiếm (20 items max)
weather_favorites.dat    ← Địa điểm yêu thích
community_reports.dat    ← Báo cáo cộng đồng
```

## 🎨 Screenshots Demo

### Tab Current
- Sky blue background khi trời quang
- Gray background khi nhiều mây
- Dark background khi mưa/bão

### Tab Details
- 8 info boxes: Humidity, Wind, Pressure, UV, Cloud, Precipitation, Sunrise, Sunset
- Nhiệt độ cảm nhận (Feels Like)

### Tab Forecast
- Biểu đồ line chart với 2 màu (red = max, blue = min)
- 7 cards hiển thị dự báo từng ngày
- Weather emoji cho mỗi ngày

### Tab Map
- HTML preview với gradient background
- Buttons link tới OpenStreetMap và Google Maps
- Browser mở map tương tác với marker

### Tab Community
- Form submit với star rating
- Table hiển thị tất cả reports
- Filter theo location

## 💡 Tips

1. **Auto-refresh**: Client tự động refresh weather mỗi 30 giây
2. **Search history**: Tìm kiếm được lưu tự động
3. **Favorites**: Dùng để truy cập nhanh địa điểm thường xem
4. **Community**: Chia sẻ feedback về độ chính xác của dự báo
5. **Map browser**: Click "Open in Browser" để xem full interactive map

## 🆘 Common Issues

### Issue: Cannot connect to server
**Solution**: 
- Đảm bảo server đã start
- Check firewall không block port 8888
- Dùng localhost nếu chạy cùng máy

### Issue: Weather data not updating
**Solution**:
- Check internet connection
- Click "Refresh" button
- Open-Meteo API có thể chậm, đợi vài giây

### Issue: Map không mở
**Solution**:
- Click "Open in Browser" thay vì xem trong app
- Đảm bảo có internet để load Leaflet.js

### Issue: Files không save được
**Solution**:
- Check quyền write trong thư mục
- Run với administrator nếu cần

## 🎉 Enjoy!

Khám phá tất cả các tính năng mới:
✅ Thông tin chi tiết đầy đủ
✅ Dự báo 7 ngày với biểu đồ đẹp
✅ Bản đồ OpenStreetMap miễn phí
✅ Chia sẻ nhiều định dạng
✅ Community reports
✅ Favorites & History
✅ Giao diện hiện đại với tabs

---
**Version**: 2.0 Advanced Features  
**Last Updated**: November 2025
