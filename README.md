# Pharmacy Kafka Microservices – 4 bài tập

**Stack:** JDK 17, Spring Boot 3.3.5, Gradle multi-project, Spring Kafka, Apache Kafka 3.9.1 (KRaft), PostgreSQL 16.

## Cấu trúc

- `pharmacy-service` – 8081, Producer `OrderEvent` JSON khi POST checkout.
- `inventory-service` – 8082, Consumer group `inventory-group`, lưu `processed_orders` và trừ tồn kho trong PostgreSQL.
- `notification-service` – 8083, Consumer group `notification-group`, log hóa đơn mô phỏng.
- `docker-compose.yml` – Kafka KRaft + PostgreSQL.
- `create-topics.bat` / `.sh` – tạo ba topics.

## Chạy trên Windows / IntelliJ

1. Cài **JDK 17**, Docker Desktop, Gradle (hoặc dùng Gradle do IntelliJ quản lý).
2. Mở thư mục gốc bằng IntelliJ, chọn JDK 17 và **Sync Gradle**.
3. Trong terminal thư mục gốc:

   ```bash
   docker compose up -d
   ```

4. Đợi Kafka khởi động xong (có thể kiểm tra `docker logs pharmacy-kafka`).
5. Chạy `create-topics.bat` trên Windows hoặc `bash create-topics.sh` trên Linux/macOS.
6. Chạy lần lượt `InventoryApplication`, `NotificationApplication`, `PharmacyApplication` từ IntelliJ. Hoặc từ Gradle:

   ```bash
   gradle :inventory-service:bootRun
   gradle :notification-service:bootRun
   gradle :pharmacy-service:bootRun
   ```

   Mỗi lệnh ở terminal riêng.

7. Test bằng `api-tests.http` trong IntelliJ.

**Lưu ý:** Nếu máy đã có PostgreSQL ở port 5432, đổi mapping port trong Compose và URL của Inventory Service tương ứng.

## Bài 1 – Topics

| Topic | Partitions | Ý nghĩa |
|---|---:|---|
| `medicine-stock-events` | 3 | Sự kiện bán thuốc/trừ kho |
| `medicine-price-updates` | 1 | Cập nhật giá |
| `pharmacy-notifications` | 2 | Thông báo |

Kiểm tra:

```bash
docker exec pharmacy-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
docker exec pharmacy-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic medicine-stock-events
```

Topic 1 partition bảo toàn thứ tự **trong partition**, không tự đảm bảo thứ tự end-to-end nếu có xử lý bất đồng bộ.

## Bài 2 – Producer

API:

```http
POST http://localhost:8081/api/v1/orders/checkout
Content-Type: application/json

{"medicineId":1,"quantity":2}
```

Producer tạo `OrderEvent(orderId,medicineId,quantity,timestamp)` rồi gọi:

```java
kafka.send("medicine-stock-events", request.medicineId().toString(), event)
```

`medicineId` là key, nên cùng key vào cùng partition **khi số partition không đổi**. API trả HTTP 202 sau khi Kafka xác nhận ghi thành công, **không có nghĩa đã trừ kho xong**.

## Bài 3 – Inventory Consumer

- `@KafkaListener(topics="medicine-stock-events", groupId="inventory-group")`
- JSON được deserialized thành `OrderEvent`.
- Bảng `medicines` được tạo bởi Hibernate.
- Tự seed: thuốc ID 1, Paracetamol, stock 100; thuốc ID 2, Vitamin C, stock 80.
- `processed_orders` ghi `orderId` để tránh trừ kho lần hai khi Kafka giao lại cùng event.
- Transaction PostgreSQL và pessimistic lock giúp tránh race condition khi trừ kho.

Kiểm tra trước và sau:

```http
GET http://localhost:8082/api/v1/medicines/1
```

Gửi quantity=2 thì stock 100 → 98 sau khi consumer xử lý.

## Bài 4 – Notification Consumer

- `@KafkaListener(topics="medicine-stock-events", groupId="notification-group")`
- Khác group với inventory nên **cả hai service đều nhận cùng một event**.
- Log:

```text
Hóa đơn cho đơn hàng [orderId] đã được gửi tới khách hàng (mô phỏng, chưa gửi email thực tế)
```

Đề chỉ bắt buộc log, vì event chưa có email khách hàng. Để gửi email thật cần bổ sung email/tra cứu khách hàng và SMTP/email provider.

## Lưu ý về xử lý lỗi và tính nhất quán

- Kafka thường là **at-least-once**: consumer group **không đảm bảo tuyệt đối không có duplicate**; bảng `processed_orders` hỗ trợ idempotency.
- Khi thuốc không tồn tại hoặc thiếu stock, listener sẽ lỗi và Spring Kafka có thể retry; nên bổ sung dead-letter topic (DLT), xử lý đơn hàng thất bại và chính sách retry trong hệ thống thật.
- Không có giao dịch phân tán giữa thanh toán và trừ kho trong ví dụ. API chỉ chứng minh **gửi sự kiện thành công**. Để xác nhận thanh toán nghiệp vụ, cần tích hợp quy trình trạng thái đơn hàng / saga / outbox.
- Password PostgreSQL trong file chỉ phục vụ bài thực hành, không dùng như vậy ở production.
- Cả ba topic đều được tạo, nhưng hai topic `medicine-price-updates` và `pharmacy-notifications` chưa có producer/consumer nghiệp vụ vì bài chỉ yêu cầu tạo topic.
