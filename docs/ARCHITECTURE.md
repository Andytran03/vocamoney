# Kiến trúc VocaMoney

Tài liệu này mô tả khung dự án sau lát **L0**. Mỗi lát sau cập nhật lại khi thêm thành phần mới. Nguồn yêu cầu: [PRD](PRD.md) §0, §8, §10.

## Tổng quan

- Android native: Kotlin, Jetpack Compose, Material 3. `minSdk 26`, `targetSdk 35`.
- MVVM + use-case + repository, trạng thái UI một chiều. Trong L0 mới có lớp dữ liệu và giao diện khung.
- Dữ liệu lưu trên máy bằng Room. Không có backend trong P0; proxy cho LLM cloud chỉ thêm từ Sprint 2 khi cần.
- DI thủ công qua `VocaMoneyApp` (một `database` dùng chung). Đổi sang Hilt khi số binding đủ lớn.

## Module Gradle

```text
android/
├── domain/   # Kotlin thuần (JVM), không phụ thuộc Android: model, quy tắc, sau này là use-case và parser
└── app/      # Ứng dụng Android: core/, data/, feature/*
```

`domain` tách riêng để test nhanh trên JVM và để logic tiền tệ, parser không phụ thuộc Android. Trong `app`, thư mục theo §10:

| Gói | Nội dung |
|---|---|
| `core/designsystem` | Màu §5 (`#137C69`, `#112B3C`, `#E7F5EF`, `#F6BC61`, `#F5F7F8`), khoảng cách 8dp, bo góc 16–20dp |
| `core/navigation` | Thanh điều hướng 4 tab: Tổng quan / Lịch sử / Ghi voice / Cài đặt. App mở ở tab Ghi voice |
| `data/local` | Room: entity, DAO, `VocaMoneyDatabase`, seed danh mục |
| `feature/*` | Màn hình theo tab. Trong L0 các màn hình chỉ báo "Sẽ có ở bản tiếp theo", không giả vờ có chức năng |

## Dữ liệu (schema Room v1)

| Bảng | Ghi chú |
|---|---|
| `categories` | 11 danh mục hệ thống seed khi tạo DB (§8). ID ổn định như `FOOD_COFFEE` |
| `voice_sessions` | Một lần bấm mic. `idempotency_key` UNIQUE để thử lại không lưu trùng (BR-04). Không lưu audio |
| `ledger_entries` | Nguồn sự thật tài chính. FK tới `voice_sessions` và `categories` |

Quy ước:
- Tiền: `Long` đơn vị đồng, không bao giờ dùng `Float`/`Double`. `AmountVnd.of()` trả `null` cho số ≤ 0 hoặc quá 100 tỷ (BR-13).
- Thời gian: epoch millis UTC. Chỉ đổi sang giờ địa phương (`Asia/Ho_Chi_Minh`) khi hiển thị hoặc khi hiểu "hôm qua", "sáng nay".
- Enum lưu dạng TEXT theo tên.
- Schema được Room xuất ra `android/app/schemas/` khi build. Từ v2 trở đi mọi thay đổi schema cần migration và test migration.

Các bảng `bank_transactions`, `bank_entry_allocations`, `processed_notifications` (L7, L8), `audit_events` (L4), `user_rules` (P1) chưa có.

## Riêng tư và bảo mật trong L0

- Chưa xin quyền nào. `RECORD_AUDIO` đến ở L3 và chỉ xin đúng lúc.
- Tắt sao lưu: `allowBackup=false` và `data_extraction_rules.xml` loại trừ mọi miền dữ liệu, để dữ liệu tài chính không rời máy qua bản sao lưu không mã hóa (§12).

## Dữ liệu mẫu

`fixtures/utterances_vi.json` chứa câu nói tiếng Việt và kết quả parser mong đợi, gồm đủ 8 dòng bảng fixture §9 và các ví dụ §1. `FixturesTest` kiểm tra mọi danh mục trong fixture đều có trong seed và mọi số tiền hợp lệ, và câu chưa rõ số tiền không sinh khoản nào.

## Build và test

Cần JDK 17+ và Android SDK (compileSdk 35).

```bash
cd android
./gradlew :domain:test :app:testDebugUnitTest   # unit test, Room test chạy bằng Robolectric
./gradlew :app:assembleDebug                    # APK debug
./gradlew -PskipAndroid=true :domain:test       # máy không có Android SDK: chỉ module domain
```

CI (`.github/workflows/android.yml`) chạy cả hai lệnh đầu cho mỗi PR và lưu báo cáo test, schema Room và APK debug làm artifact.
