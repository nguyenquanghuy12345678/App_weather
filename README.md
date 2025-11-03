# App Weather (Java)

Ứng dụng Weather (App Weather) là một dự án Java dạng client-server dùng RMI/sockets để client truy vấn dữ liệu thời tiết từ server. Repository này chứa mã nguồn cho client, server và các lớp chia sẻ (shared) ở dạng module Java.

## Tổng quan

- Mục tiêu: minh hoạ một ứng dụng thời tiết phân tán với phần mềm client (UI) và server (dịch vụ cung cấp dữ liệu/geo-coding). Ứng dụng phù hợp cho học tập, demo và mở rộng.
- Ngôn ngữ: Java (sử dụng module system — có `module-info.java`).

## Tính năng chính

- Client: giao diện đăng nhập, gửi yêu cầu vị trí, hiển thị thông tin thời tiết.
- Server: xử lý yêu cầu client, geocoding (chuyển vị trí thành thông tin), lưu/truy xuất dữ liệu thời tiết.
- Các lớp `shared` chứa message và cấu trúc dữ liệu dùng chung.

## Cấu trúc thư mục (tổng quan)

- `src/` — mã nguồn Java theo mô-đun
  - `client/` — mã nguồn client (ví dụ: `LoginDialog.java`, `WeatherClient.java`, `WeatherPanel.java`)
  - `server/` — mã nguồn server (ví dụ: `WeatherServer.java`, `ClientHandler.java`, `WeatherData.java`)
  - `shared/` — lớp và hằng số dùng chung (ví dụ: `Constants.java`, `Message.java`, `LocationData.java`)
- `bin/` — (tùy phiên bản) chứa class đã biên dịch (nếu có)
- `resources/` — tài nguyên (icon, config, v.v.)

> Ghi chú: Cấu trúc chi tiết có thể thay đổi theo commit; xem folder `src/` để biết tên package và lớp chính xác.

## Yêu cầu trước

- JDK 11 hoặc mới hơn (do có `module-info.java`, khuyến nghị JDK 11+ hoặc JDK 17).
- (Tùy chọn) IDE: Eclipse, IntelliJ IDEA hoặc NetBeans để import dễ dàng.

## Hướng dẫn build & chạy (Windows PowerShell)

Hai cách phổ biến: dùng IDE (khuyên dùng) hoặc dòng lệnh.

1) Bằng IDE (Eclipse / IntelliJ):

- Import project như một Java Project (hoặc module project)
- Đặt `module-path`/`class-path` nếu cần và chạy `WeatherServer` (server) và `WeatherClient` (client) riêng biệt.

2) Dòng lệnh (ví dụ nhanh, tuỳ cấu trúc package thực tế):

- Mở PowerShell ở thư mục gốc `App_weather`.
- Tạo thư mục đầu ra và biên dịch tất cả file Java:

```powershell
# Tạo thư mục out để chứa các class biên dịch
New-Item -ItemType Directory -Force -Path out

# Lấy danh sách file .java và biên dịch vào out
$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object -ExpandProperty FullName
javac -d out $files
```

- Chạy server (giả sử class main nằm ở `server.WeatherServer`; thay tên module/class phù hợp theo mã của bạn):

```powershell
# Ví dụ chạy (thay đổi 'module/name' theo module và fully-qualified main class của bạn)
java -cp out server.WeatherServer
```

- Chạy client (tương tự):

```powershell
java -cp out client.WeatherClient
```

Ghi chú: nếu project sử dụng module system với tên module (có `module-info.java`), chạy có thể cần tham số `--module-path` và `-m module/name`. Kiểm tra `module-info.java` để biết tên module và định danh main.

## Cấu hình

- Tệp cấu hình (nếu có): kiểm tra thư mục `resources/` hoặc các hằng số trong `shared/Constants.java` để biết port mặc định, host, hay API keys (ví dụ nếu dùng dịch vụ geocoding bên ngoài).
- Mặc định, server lắng nghe trên một port (ví dụ 8000 hoặc theo `Constants`). Nếu port bị chiếm dụng, chỉnh lại trước khi chạy.

## Kiểm thử nhanh

1. Khởi chạy server.
2. Khởi chạy client.
3. Đăng nhập (nếu có) hoặc nhập một vị trí (ví dụ: "Hanoi, VN") vào client.
4. Kiểm tra client nhận được dữ liệu thời tiết và hiển thị.

Nếu có log/console, xem thông báo lỗi để biết chi tiết (connect, port, hoặc lỗi JSON/parsing).

## Gỡ lỗi

- Sử dụng IDE để đặt breakpoint trên `WeatherServer.main` và `WeatherClient.main`.
- Kiểm tra port, firewall (Windows) nếu client không kết nối được server.

## Cách đóng góp

- Fork repo / clone và mở pull request.
- Viết test (nếu thêm logic quan trọng), cập nhật README nếu thay đổi cách chạy.

## Các điểm mở rộng (gợi ý)

- Thêm tính năng caching dữ liệu thời tiết.
- Lưu lịch sử truy vấn vào DB (MySQL / SQLite).
- Thêm chế độ đa luồng cho server để phục vụ nhiều client song song.

## License

Đặt license phù hợp cho dự án (ví dụ: MIT). Nếu bạn muốn mình thêm file `LICENSE`, cho biết loại license.

## Liên hệ

- Tác giả / Người quản lý repo: xem thông tin trong `README.md` gốc của workspace hoặc file `INDEX.txt`.

---

Nếu bạn muốn, mình có thể: tạo script build/run cụ thể (PowerShell) dựa trên tên package/class thực tế trong `src/` để chạy tự động — cho mình biết bạn muốn mình tự động phát hiện và thêm script không.