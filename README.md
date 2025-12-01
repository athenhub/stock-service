# 🏗️ AthenHub Stock Service

AthenHub Stock Service는 **재고 관리**, **재고 감소 처리**, **동시성 제어**,  
**이벤트 기반 재고 처리(Saga Pattern)**, **RabbitMQ 메시지 기반 아키텍처**를 구현한  
도메인 중심(DDD) 재고 서비스입니다.

재고 감소 요청을 안정적으로 처리하고, 실패 시 보상 트랜잭션을 자동 유도하며,  
대규모 트래픽에서도 안정적인 처리를 목표로 설계되었습니다.

# 1. 📘 Overview

AthenHub Stock Service는 다음 핵심 요구사항을 충족하도록 설계되었습니다.

- 재고 감소 로직에 대한 **낙관적 락(Optimistic Lock)** 기반 동시성 제어
- **멱등성(Idempotency)** 보장 (중복 요청 방지)
- RabbitMQ 기반 **재고 감소 성공/실패 이벤트 발행**
- **Retry / DLQ 전략**을 통한 안정적인 메시지 처리
- 주문 서비스(Order Service)와 연동된 **Saga 패턴** 구현
- 재고 감소 이력 추적 (StockHistory)
- DDD 기반 패키지 구조

# 2. 🏛 Architecture

## 📌 High-level Architecture

```
 Order Service
     │
     │ OrderCreatedEvent
     ▼
 Stock Service
 ├─ 재고 감소 처리(Optimistic Lock)
 ├─ 멱등성 검사(History 존재 여부)
 ├─ 성공 시 StockDecreaseSuccessEvent 발행
 ├─ 실패 시 Retry → DLQ
 └─ DLQ Listener → OrderProcessFailedEvent 발행
     ▼
 Order Service
 └─ 보상 트랜잭션(주문 취소 등)
```

# 3. 📦 Package Structure

```
com.athenhub.stockservice
 ├── application
 │    ├── dto/                    → StockDecreaseBatchEvent, StockDecreaseRequest 등
 │    ├── service/                → StockDecreaseHandler, EventPublisher 인터페이스
 │    └── event/external/         → 성공/실패 외부 이벤트
 │
 ├── domain
 │    ├── Stock, StockHistory
 │    ├── vo(OrderId, ProductVariantId)
 │    ├── exception/
 │    └── repository/
 │
 ├── infrastructure
 │    ├── rabbitmq
 │    │     ├── publish/          → Rabbit Event Publish 구현체
 │    │     ├── subscribe/        → 재고 감소 이벤트 Listener
 │    │     ├── error/            → StockErrorType
 │    │     └── config/           → RabbitMQ 설정
 │    └── config/                 → 공통 MessageConverter
 │
 ├── api
 │    └── controller … (필요시)
 │
 └── StockServiceApplication.java
```

# 4. 🧱 Domain Model

### 📌 Stock

- 개별 Variant(ProductVariant)의 재고 수량을 보유
- `decrease(quantity)` 내부에서 비즈니스 규칙 검증
- Optimistic Lock @Version 적용 가능

### 📌 StockHistory

- 모든 재고 감소 기록 저장
- OrderId + VariantId + Quantity 조합으로 재고 흐름 추적
- 멱등성 구현 핵심 요소 (`existsByOrderId`)

### 📌 Value Objects

- `OrderId`
- `ProductVariantId`

# 5. 🚦 Message Flow (RabbitMQ)

## 📌 1) 재고 감소 처리 성공

```
OrderCreatedEvent
      ▼
StockDecreaseBatchEvent
      ▼
StockDecreaseHandler
      ▼
StockDecreaseSuccessEventPublisher
      ▼
RabbitMQ → decrease.success.routingKey
```

Order 서비스는 이 메시지를 수신해 다음 단계(예: 결제 요청)를 진행.

## 📌 2) 재고 감소 처리 실패

### ❗ 재고 부족 (OUT_OF_STOCK)

→ 즉시 DLQ 이동 → OrderProcessFailedEvent 발행

### ❗ Optimistic Lock 충돌

→ RetryQueue로 재전송(N회) → 실패 시 DLQ 이동

# 6. 🔁 Retry / DLQ Strategy

**RetryManager**가 전체 흐름을 책임진다.

### ✔ Retry 조건

- `OptimisticLockException`
- 일시적 DB Lock
- 네트워크 문제

### ✔ Retry 메시지 헤더

```
x-retry-count: 현재 재시도 횟수
```

### ✔ DLQ 메시지 헤더

```
x-retry-count
x-error-type: OUT_OF_STOCK | RETRY_EXCEEDED | UNKNOWN_ERROR
```

### ✔ DLQ Listener

DLQ 메시지를 기반으로 Order 서비스에 전달할  
`OrderProcessFailedEvent`를 생성 후 발행.

# 7. 🔐 Concurrency Control — Optimistic Lock

### ✔ 왜 Optimistic Lock인가?

- 재고 감소는 동시성이 극도로 많이 발생하는 구간
- 비관적 락(Pessimistic Lock)은 성능 병목 발생
- Optimistic Lock 충돌은 Retry를 통해 해결 가능

Stock 엔티티 예:

```java

@Version
private Long version;
```

# 8. 🔄 Idempotency Strategy

중복된 주문 이벤트 처리 방지를 위해:

### ✔ 멱등성 키: OrderId

### ✔ 검증 로직

```
if (stockHistoryRepository.existsByOrderId(order))
    return;
```

이미 처리한 주문이라면 재고 감소를 절대로 다시 하지 않음.

# 9. 🧪 Event Publishing

### ✔ 성공 이벤트

`StockDecreaseSuccessEventPublisher` → RabbitStockDecreaseSuccessEventPublisher

### ✔ 실패 이벤트

`StockDecreaseFailedEventPublisher` → RabbitStockDecreaseFailedEventPublisher

### ✔ DLQ 이벤트

`OrderProcessFailedEvent`로 변환 후 Order 서비스 전달

# 10. 🛠 Tech Stack

| Layer             | Tech                |
|-------------------|---------------------|
| Language          | Java 21             |
| Framework         | Spring Boot 3.x     |
| Build             | Gradle              |
| Messaging         | RabbitMQ            |
| DB                | MySQL               |
| ORM               | JPA/Hibernate       |
| Architecture      | Hexagonal + DDD     |
| Concurrency       | Optimistic Lock     |
| Messaging Pattern | Saga / Event-driven |

# 11. 📄 주요 기능 요약

- 재고 감소 처리(단일/배치)
- 재고 감소 이력 저장
- 멱등성 처리
- 재고 부족 감지
- Retry / DLQ 메시지 관리
- 재고 감소 성공/실패 이벤트 발행
- Order 서비스와 Saga 연동
- MessageConverter(Jackson) 기반 JSON 직렬화

# 12. 📚 Development Guidelines

- 모든 이벤트는 JSON 형태로 전송
- 메시지 헤더는 표준 키 사용
    - `x-retry-count`
    - `x-error-type`
- 메시지 객체는 반드시 record 기반 불변 구조 사용
- Service Layer는 반드시 트랜잭션 단위로 처리
- 도메인 규칙은 Entity/VO 내부에서 수행
- 공통 예외는 AbstractServiceException 기반 확장

# 13. 🔍 Diagram (Text Version)

### 재고 감소 Saga 흐름

```
OrderCreatedEvent
     ▼
StockDecreaseBatchEvent Listener
     ▼
decreaseAll()
     ├─ 멱등성 검사
     ├─ 재고 감소 (Optimistic Lock)
     ├─ StockHistory 저장
     └─ 성공 이벤트 발행
     ▼
StockDecreaseSuccessEvent
     ▼
Order 서비스 소비 → 다음 단계 진행
```

### 실패 흐름

```
StockDecreaseHandler
       ▼
    Failure
       ▼
RetryManager
  ├─ RetryQueue (N회)
  └─ DLQ 이동
       ▼
DLQ Listener
       ▼
OrderProcessFailedEvent
       ▼
Order 서비스 보상 트랜잭션 실행
```

# ✨ Author

**AthenHub Backend Developer — 김지원**  
Stock 이벤트 처리, 재고 감소 트랜잭션, 메시징 아키텍처 담당

