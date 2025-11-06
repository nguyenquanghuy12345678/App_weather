# Hướng Dẫn Thay Thế Icons

## Icons Đã Tải Về

Các file icon đã được tải về tự động từ Weather Icons (SVG format):

- `sun.png` - Trời nắng/quang đãng ☀
- `partly_cloudy.png` - Có mây ⛅
- `cloudy.png` - U ám/nhiều mây ☁
- `rain.png` - Mưa 🌧
- `storm.png` - Giông bão ⛈
- `snow.png` - Tuyết ❄
- `fog.png` - Sương mù 🌫
- `error.png` - Lỗi/không có dữ liệu ❌
- `default.png` - Icon mặc định 🌤

## Thay Thế Bằng Icons Tùy Chỉnh

Nếu bạn muốn sử dụng các icon PNG đẹp hơn, hãy làm theo các bước sau:

### 1. Tìm Icons Miễn Phí

Các nguồn icon miễn phí tốt:
- **Flaticon**: https://www.flaticon.com/free-icons/weather
- **Icons8**: https://icons8.com/icons/set/weather
- **Freepik**: https://www.freepik.com/free-icons/weather
- **IconFinder**: https://www.iconfinder.com/free_icons/weather

### 2. Yêu Cầu Cho Icons

- **Định dạng**: PNG với nền trong suốt
- **Kích thước**: 120x120 pixels trở lên (càng lớn càng sắc nét)
- **Phong cách**: Nên chọn cùng một bộ icons để đồng nhất

### 3. Tải Và Thay Thế

1. Tải icons về máy
2. Đổi tên theo đúng tên file trong thư mục `resources/icons/`
3. Copy/paste vào thư mục `resources/icons/` (ghi đè file cũ)

### 4. Icons Cần Thiết

Đảm bảo có đủ 9 file icons sau:

| File Name | Mô Tả | Điều Kiện Hiển Thị |
|-----------|-------|-------------------|
| `sun.png` | Nắng | clear, sunny |
| `partly_cloudy.png` | Có mây | partly cloudy |
| `cloudy.png` | U ám | cloudy |
| `rain.png` | Mưa | rain, drizzle |
| `storm.png` | Bão | storm, thunder |
| `snow.png` | Tuyết | snow |
| `fog.png` | Sương mù | fog |
| `error.png` | Lỗi | error, not found, unavailable |
| `default.png` | Mặc định | Các trường hợp khác |

## Thay Đổi Kích Thước Icons

Nếu muốn thay đổi kích thước hiển thị của icons, sửa trong file `WeatherPanel.java`:

```java
private static final int ICON_SIZE = 120; // Thay đổi số này (đơn vị: pixels)
```

## Chuyển Đổi SVG sang PNG

Nếu có file SVG và muốn chuyển sang PNG:

### Sử dụng PowerShell với ImageMagick:

```powershell
# Cài đặt ImageMagick (nếu chưa có)
# Download từ: https://imagemagick.org/script/download.php

# Chuyển đổi
magick convert -background none -size 256x256 input.svg output.png
```

### Sử dụng Online Tool:

- https://cloudconvert.com/svg-to-png
- https://convertio.co/svg-png/

## Ghi Chú

- Icons hiện tại là SVG nên có thể không hiển thị tốt trong Java Swing
- Nên chuyển sang PNG để hiển thị tốt nhất
- Icons được scale tự động theo `ICON_SIZE` đã định nghĩa
