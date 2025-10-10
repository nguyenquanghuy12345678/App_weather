# Hướng dẫn Test Weather App

## Các vấn đề đã sửa:

### 1. ✅ Sửa lỗi API trả về 0
- **Vấn đề**: JSON parsing không đúng, không tìm được giá trị trong object "current"
- **Giải pháp**: 
  - Cải thiện method `parseJsonDouble()` để tìm kiếm trong object "current"
  - Thêm debug logging chi tiết để theo dõi quá trình parsing
  - Xử lý whitespace và format JSON đúng cách

### 2. ✅ Cho phép nhập địa điểm
- **Trước**: Chỉ chọn từ dropdown list
- **Sau**: 
  - ComboBox giờ là editable, có thể gõ trực tiếp
  - Có placeholder text "Nhập tên thành phố..."
  - Vẫn giữ danh sách gợi ý 10 địa điểm phổ biến

### 3. ✅ Thêm Geocoding API
- **Chức năng mới**: `GeocodingService.java`
- Tự động tìm tọa độ (latitude, longitude) từ tên địa điểm
- Sử dụng Open-Meteo Geocoding API (miễn phí)
- Hỗ trợ tìm kiếm bất kỳ thành phố nào trên thế giới

## Cách test:

### Test 1: Kiểm tra API trả về đúng dữ liệu
1. Start `WeatherServer.java`
2. Start `WeatherClient.java`
3. Login
4. Click "Refresh Weather" (hoặc tự động load)
5. **Kiểm tra console server** - phải thấy:
   ```
   Fetching weather from: https://api.open-meteo.com/v1/forecast?...
   API Response Code: 200
   API Response: {"latitude":16.05,"longitude":108.2,"current":{...}}
   Parsing temperature_2m = 28.5
   Parsed successfully: 28.5
   Parsing relative_humidity_2m = 75
   Parsed successfully: 75
   ...
   Weather data fetched successfully!
   ```
6. **Kiểm tra UI** - phải hiển thị:
   - Nhiệt độ thực (VD: 28.5°C, không phải 0°C)
   - Độ ẩm thực (VD: 75%, không phải 0%)
   - Tốc độ gió thực (VD: 12.3 km/h, không phải 0 km/h)
   - Điều kiện thời tiết (Clear Sky, Cloudy, Rainy, etc.)

### Test 2: Tìm kiếm địa điểm từ danh sách
1. Click vào ComboBox
2. Chọn một địa điểm (VD: "Tokyo, Japan")
3. Click nút "Tìm kiếm"
4. Kiểm tra thời tiết Tokyo hiển thị

### Test 3: Nhập địa điểm tùy ý
1. Click vào ComboBox
2. **Gõ tên thành phố** (VD: "Seoul", "Bangkok", "Sydney")
3. Click "Tìm kiếm"
4. **Kiểm tra console server** - phải thấy:
   ```
   Geocoding location: Seoul
   Geocoding API Response Code: 200
   Found location: Seoul, South Korea (37.566, 126.9784)
   Fetching weather from: https://api.open-meteo.com/v1/forecast?latitude=37.5660&longitude=126.9784...
   ```
5. Kiểm tra UI hiển thị thời tiết Seoul

### Test 4: Nhập địa điểm tiếng Việt
1. Nhập: "Hà Nội", "Đà Nẵng", "Sài Gòn"
2. Click tìm kiếm
3. Phải tìm được và hiển thị thời tiết

### Test 5: Nhập địa điểm không tồn tại
1. Nhập: "asdfghjkl" (địa điểm không có thật)
2. Click tìm kiếm
3. Phải hiển thị: 
   - Condition: "Location not found"
   - Icon: ❌
   - Các giá trị khác: 0 hoặc N/A

## Debug khi có lỗi:

### Nếu vẫn hiển thị 0°C:
1. Kiểm tra console server xem có lỗi gì
2. Kiểm tra log "API Response:" - xem JSON có đúng không
3. Kiểm tra log "Parsing xxx" - xem parse có thành công không
4. Test API trực tiếp trong browser:
   ```
   https://api.open-meteo.com/v1/forecast?latitude=16.0544&longitude=108.2022&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m
   ```

### Nếu không tìm được địa điểm:
1. Kiểm tra console server có log "Geocoding location: ..."
2. Kiểm tra "Geocoding API Response Code" phải là 200
3. Test Geocoding API trực tiếp:
   ```
   https://geocoding-api.open-meteo.com/v1/search?name=Seoul&count=1&language=en&format=json
   ```

### Nếu không kết nối được server:
1. Kiểm tra firewall
2. Kiểm tra port 8888 có bị chiếm không
3. Kiểm tra server có đang chạy không

## Các tính năng đã có:

✅ Lấy thời tiết thực từ Open-Meteo API  
✅ Tìm kiếm địa điểm bằng cách chọn từ list  
✅ Tìm kiếm địa điểm bằng cách nhập tên  
✅ Tự động tìm tọa độ từ tên địa điểm (Geocoding)  
✅ Hỗ trợ tìm kiếm bằng tiếng Việt  
✅ Auto refresh mỗi 30s  
✅ Icon thời tiết động (☀️🌧️⛈️❄️🌫️)  
✅ Màu nền thay đổi theo thời tiết  
✅ Debug logging chi tiết  
✅ Error handling tốt  

## Danh sách địa điểm có sẵn:
- Da Nang, Vietnam
- Ho Chi Minh City, Vietnam
- Hanoi, Vietnam
- Tokyo, Japan
- Seoul, South Korea
- Bangkok, Thailand
- Singapore
- New York, USA
- London, UK
- Paris, France

Bạn cũng có thể nhập BẤT KỲ thành phố nào khác!
