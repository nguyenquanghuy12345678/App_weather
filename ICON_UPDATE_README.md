# Cập Nhật: Chuyển Đổi Weather Icons từ Emoji sang Hình Ảnh

## Thay Đổi Đã Thực Hiện

### 1. Tải Về Icons
- Đã tạo script `download_icons.ps1` để tự động tải các weather icons từ Icons8
- Các icons đã được tải về định dạng PNG (256x256 pixels)
- Lưu trữ tại: `resources/icons/`

### 2. Cập Nhật Code

**File: `src/client/WeatherPanel.java`**

#### Thêm Mới:
```java
private static final int ICON_SIZE = 120; // Kích thước hiển thị icon
```

#### Thay Đổi Khởi Tạo Weather Icon:
```java
// CŨ:
lblWeatherIcon = new JLabel("☀");
lblWeatherIcon.setFont(new Font("Segoe UI", Font.PLAIN, 120));

// MỚI:
lblWeatherIcon = new JLabel();
lblWeatherIcon.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
setWeatherIcon("resources/icons/sun.png"); // Default icon
```

#### Thêm Method Mới:
```java
private void setWeatherIcon(String iconPath) {
    try {
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
        lblWeatherIcon.setIcon(new ImageIcon(image));
    } catch (Exception e) {
        // Fallback to text if icon not found
        lblWeatherIcon.setText("?");
        lblWeatherIcon.setFont(new Font("Segoe UI", Font.PLAIN, 120));
    }
}
```

#### Cập Nhật Method updateWeather():
Thay thế tất cả `lblWeatherIcon.setText()` bằng `setWeatherIcon()`:

| Điều Kiện | Emoji Cũ | Icon Mới |
|-----------|----------|----------|
| clear/sunny | ☀ | `resources/icons/sun.png` |
| partly cloudy | ⛅ | `resources/icons/partly_cloudy.png` |
| cloudy | ☁ | `resources/icons/cloudy.png` |
| rain/drizzle | 🌧 | `resources/icons/rain.png` |
| storm/thunder | ⛈ | `resources/icons/storm.png` |
| snow | ❄ | `resources/icons/snow.png` |
| fog | 🌫 | `resources/icons/fog.png` |
| error | ❌ | `resources/icons/error.png` |
| default | 🌤 | `resources/icons/default.png` |

## Cách Sử Dụng

### Bước 1: Tải Icons (Nếu Chưa Có)
```powershell
.\download_icons.ps1
```

### Bước 2: Compile và Chạy
```bash
# Compile
javac -d bin src/**/*.java

# Chạy Server
java -cp bin server.WeatherServer

# Chạy Client
java -cp bin client.WeatherClient
```

## Lợi Ích

✅ **Hiển thị đúng trên mọi hệ thống** - Không còn vấn đề □ hoặc �
✅ **Icons đẹp và chuyên nghiệp** - Sử dụng icons PNG chất lượng cao
✅ **Dễ tùy chỉnh** - Có thể thay đổi icons bất kỳ lúc nào
✅ **Giữ nguyên logic** - Tất cả code và logic xử lý không thay đổi
✅ **Tương thích tốt** - Java Swing hỗ trợ PNG natively

## Tùy Chỉnh

### Thay Đổi Kích Thước Icons
Sửa trong `WeatherPanel.java`:
```java
private static final int ICON_SIZE = 150; // Thay đổi số này
```

### Thay Thế Icons
1. Tìm icons PNG mới (khuyên dùng 256x256 pixels trở lên)
2. Đổi tên đúng theo các file hiện có
3. Copy vào `resources/icons/` (ghi đè file cũ)

### Thêm Icon Mới
Nếu muốn thêm điều kiện thời tiết mới:

1. Thêm file PNG vào `resources/icons/`
2. Cập nhật method `updateWeather()`:
```java
} else if (condition.contains("your_condition")) {
    setWeatherIcon("resources/icons/your_icon.png");
    setBackground(new Color(R, G, B));
}
```

## Ghi Chú

- Icons hiện tại từ Icons8 (miễn phí cho sử dụng cá nhân)
- Cho mục đích thương mại, vui lòng kiểm tra: https://icons8.com/license
- Có thể thay thế bằng icons từ nguồn khác (Flaticon, Freepik, etc.)

## Files Liên Quan

- `src/client/WeatherPanel.java` - Code chính đã được cập nhật
- `resources/icons/` - Thư mục chứa các icons
- `download_icons.ps1` - Script tự động tải icons
- `ICON_GUIDE.md` - Hướng dẫn chi tiết về icons
- `convert_icons.ps1` - Script chuyển đổi SVG sang PNG (nếu cần)

## Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra thư mục `resources/icons/` có đầy đủ 9 file PNG
2. Đảm bảo đường dẫn relative đúng (chạy từ root project)
3. Kiểm tra file PNG không bị lỗi (có thể mở bằng image viewer)
