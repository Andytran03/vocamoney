# VocaMoney

> **Nói một câu. Ghi một khoản. Hiểu mọi đồng tiền.**

VocaMoney là ứng dụng Android ghi chi tiêu bằng giọng nói tiếng Việt. Người dùng bấm nút mic, nói tự nhiên như "Mua cà phê 35 nghìn", xem lại bản nháp, sửa nếu cần, rồi lưu vào sổ chi tiêu trên máy.

**Trạng thái:** đang ở giai đoạn đặc tả, chưa có mã nguồn. Đặc tả đầy đủ nằm ở [docs/PRD.md](docs/PRD.md).

## Ví dụ

| Câu nói | Kết quả mong đợi |
|---|---|
| "Mua cà phê 35 nghìn." | 1 khoản chi 35.000 ₫, danh mục `FOOD_COFFEE` |
| "Sáng nay mua bánh mì 25 nghìn và cà phê 35 nghìn." | 2 khoản chi, tổng 60.000 ₫ |
| "Đổ xăng xe máy 50 nghìn tiền mặt." | Khoản chi tiền mặt 50.000 ₫, `TRANSPORT_FUEL` |
| "85 nghìn vừa chuyển khoản là tiền ăn trưa." | Liên kết với giao dịch ngân hàng đang chờ, không tạo thêm khoản chi |
| "Nhận lương 15 triệu." | Khoản thu, không phải khoản chi |

## Phạm vi MVP (P0)

- Bấm mic → nhận dạng giọng nói (ASR) → phân tích ý định → xem trước và sửa → lưu.
- Nhiều khoản trong một câu nói; nhập bằng bàn phím khi không dùng được giọng nói.
- Sổ chi tiêu ngoại tuyến, lịch sử, sửa/xóa có nhật ký, dashboard theo ngày/tháng/danh mục.
- Đọc thông báo ngân hàng là **tùy chọn**, chỉ bật khi người dùng đồng ý, có adapter giả lập để test.

## Nguyên tắc bất biến

- Chỉ ghi âm khi người dùng chủ động bấm mic. Không nghe nền, không lưu audio thô mặc định.
- Không tự bịa số tiền, không sửa số tiền từ ngân hàng, không đếm trùng giao dịch giọng nói và ngân hàng.
- Kết quả từ LLM luôn được kiểm tra (số tiền, ý định, danh mục, ngày) trước khi lưu.
- Dữ liệu lưu cục bộ trước. Không bao giờ hỏi mật khẩu hay OTP ngân hàng.
- Tiền lưu bằng số nguyên VND, không dùng Float/Double. Thời gian lưu UTC, hiển thị theo `Asia/Ho_Chi_Minh`.

## Công nghệ dự kiến

Kotlin · Jetpack Compose · Material 3 · MVVM + use-case + repository · Room · WorkManager (khi thật sự cần). ASR và parser là adapter có thể thay thế, kèm bản giả lập tất định để phát triển và test. Backend chỉ thêm khi dùng parser trên cloud, và khóa API không bao giờ nằm trong APK.

## Cấu trúc thư mục dự kiến

```text
vocamoney/
├── README.md
├── docs/            # PRD, kiến trúc, quyền riêng tư, kế hoạch test
├── android/         # ứng dụng Android (core, data, domain, feature/*)
├── backend/         # chỉ khi dùng parser trên cloud
└── fixtures/        # câu nói mẫu tiếng Việt, thông báo ngân hàng giả lập
```

## Lộ trình

| Sprint | Mục tiêu |
|---|---|
| 1 | Khung dự án, design system, giọng nói giả lập, Room, parser tất định |
| 2 | ASR thật, nhiều khoản trong một câu, kiểm tra hợp lệ, sửa, lịch sử |
| 3 | Đọc thông báo ngân hàng (tùy chọn), ghép giao dịch, dashboard |
| 4 | QA, trợ năng, gia cố, beta kín |

Chi tiết release gate và bộ test TC-01 đến TC-28 nằm trong [docs/PRD.md](docs/PRD.md).
