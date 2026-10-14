# Campus Loop

Campus Loop là bản mở rộng nhỏ từ [Campus Hub Base](https://github.com/dinhthanhnam/campus-hub-base). Ngoài bảng tin, tìm kiếm, bình luận và bày tỏ quan tâm có sẵn, người dùng đăng nhập có thể lưu bài viết vào danh sách cá nhân để xem lại.

## Chức năng mới

- Lưu và bỏ lưu từ thẻ bài viết hoặc trang chi tiết.
- Xem danh sách bài đã lưu theo thời điểm lưu mới nhất.
- Danh sách riêng theo tài khoản; một người không thể đọc bài đã lưu của tài khoản khác.
- Migration Flyway tạo bảng `saved_posts`, unique constraint cho cặp người dùng/bài viết và khóa ngoại tự dọn khi bài bị xóa.

## Công nghệ

- Frontend: React + Vite
- Backend: Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security
- Cơ sở dữ liệu: MySQL 8.4, Flyway
- Chạy cục bộ: Docker Compose cho MySQL/backend và Vite cho frontend

## Chạy cục bộ

Cần JDK 21, Node.js và Docker Compose. Tạo tệp `.env` riêng từ `.env.example`, thay các giá trị mẫu bằng mật khẩu riêng, rồi chạy từ thư mục gốc:

```powershell
Copy-Item .env.example .env
# Mở .env và thay các giá trị mẫu

docker compose up --build
```

Mở terminal khác:

```powershell
cd frontend
npm ci
npm run dev
```

Mở `http://localhost:5173`. Vite chuyển tiếp `/api` tới backend cổng 8080. Kiểm tra backend tại `http://localhost:8080/actuator/health`.

Tài khoản mẫu `student` và `admin` chỉ được tạo khi đặt `DEMO_STUDENT_PASSWORD` và `DEMO_ADMIN_PASSWORD` trong `.env` riêng. Nếu không cấu hình, người dùng có thể tự đăng ký; mã nguồn không chứa mật khẩu đăng nhập mặc định.

## API bổ sung

| Phương thức | Đường dẫn | Mục đích |
|---|---|---|
| `GET` | `/api/saved-posts` | Lấy bài đã lưu của tài khoản đăng nhập |
| `PUT` | `/api/saved-posts/{postId}` | Lưu bài viết; gọi lại không tạo bản ghi trùng |
| `DELETE` | `/api/saved-posts/{postId}` | Bỏ lưu bài viết |

Ba API đều yêu cầu HTTP Basic. Danh sách chỉ truy vấn theo người dùng hiện tại trên máy chủ.

## Kiểm tra

```powershell
cd frontend
npm ci
npm run lint
npm run build

cd ../backend
./gradlew.bat test
```

Dự án yêu cầu JDK 21. Nếu Windows báo `JAVA_HOME` không hợp lệ, cài JDK 21 và cập nhật biến môi trường trước khi chạy Gradle.

## Triển khai

Chưa có kho GitHub nhóm hoặc URL công khai trong bản này. Trước khi trình bày, tạo repository, push mã nguồn, triển khai frontend/backend/MySQL, cấu hình `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS` và `VITE_API_URL` theo dịch vụ đã chọn, sau đó kiểm tra luồng lưu bài bằng dữ liệu thật. Dùng HTTPS, mật khẩu riêng và secret manager/environment variables; đừng commit credentials. Chỉ cấu hình `DEMO_ADMIN_PASSWORD` hoặc `DEMO_STUDENT_PASSWORD` nếu thực sự cần tài khoản mẫu trên môi trường demo.

Điền URL repository, URL ứng dụng và commit hash sau khi triển khai vào báo cáo `Bao_cao_Mini_Project_Campus_Loop.docx`.
