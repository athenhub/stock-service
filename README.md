# 재고 서비스 (Stock Service)

## 개요(Overview)

본 서비스는 Spring Boot 기반으로 구현된 **재고 관리 마이크로서비스**로, 다음과 같은 기술적 요구 사항을 충족하도록 설계되었습니다:

- **낙관적 락(Optimistic Locking)** 기반 동시성 제어
- **멱등성(Idempotency)** 보장
- **RabbitMQ 기반 이벤트 드리븐 아키텍처**
- **DDD(Domain-Driven Design)** 기반 도메인 모델링

## 아키텍처 구성(Architecture)

- `Stock`, `StockHistory` 중심의 **도메인 모델** 구성
- 재고 엔티티에 `@Version`을 적용하여 **낙관적 락 기반의 동시성 제어**
- 주문별 재고 감소 요청에 대해 **이력 기반 멱등성 보장**
- RabbitMQ를 통한 **비동기 이벤트 발행/구독**
- `OrderCreated` 이벤트 수신 → 재고 감소 처리 → `StockDecreased` 이벤트 발행

## 핵심 처리 흐름(Core Flow)

1. RabbitMQ 로부터 `OrderCreatedMessage` 수신
2. 메시지를 `StockDecreaseRequest` 로 변환
3. Service Layer 처리:
    - 멱등성 검증(이미 처리된 주문인지 확인)
    - 재고 엔티티 조회(+ Optimistic Lock Version 확인)
    - 재고 차감
    - 재고 이력 저장
    - `StockDecreasedEvent` 발행
4. 일시적 오류에 대해서 RabbitMQ 재시도 정책 적용

## 기술적 구현 포인트(Technical Highlights)

- **Optimistic Locking**
    - JPA `@Version` 기반으로 재고 동시 감소 요청을 안전하게 처리
- **멱등성 보장**
    - `(orderId, productVariantId)` 를 유니크 키로 관리하여 중복 감소 차단
- **RabbitMQ 이벤트 처리**
    - `RabbitListener`, `RabbitTemplate` 기반의 이벤트 송/수신
- **Feign Client**
    - 상품 서비스/회원 서비스와의 통신 시 Custom ErrorDecoder 적용
- **공통 에러 포맷 통합**
    - `common-core` 의 `GlobalErrorCode` 기반 API 표준화
- **테스트 전략**
    - `SpringBootTest` + ApplicationEvents 를 활용한 도메인 이벤트 검증

## DDD 아키텍처 도입 — 도메인 중심의 책임 분리
```
stockservice/
  ├── application/
  │     ├── service/StockDecreaseService
  │     ├── eventhandler/StockDecreaseHandler
  │     └── dto/
  ├── domain/
  │     ├── Stock, StockHistory
  │     ├── event/internal
  │     └── repository/
  ├── infrastructure/
  │     ├── rabbitmq/
  │     ├── client/product
  │     └── client/member
  └── StockServiceApplication.java
```

1. domain 계층 — 순수한 비즈니스 규칙

- Stock, StockHistory 엔티티 중심
- 재고 감소/검증 로직 등 비즈니스 규칙이 응용 계층에 노출되지 않도록 보호
- 도메인 이벤트(StockDecreasedEvent)를 정의하여 상태 변화 자체를 모델링

2. application 계층 — use-case 구현

- StockDecreaseService, StockDecreaseHandler 등
- 트랜잭션 단위 정의, 도메인 모델 조립, 외부 서비스(상품/회원) 활용
- 도메인 계층은 외부 세상과의 연결 책임을 갖지 않음

3. infrastructure 계층 — 외부 시스템 통신

- RabbitMQ 메시징 처리
- Feign Client 통한 상품/회원 서비스 호출
- 외부 의존성 구현체만 존재하며, 비즈니스 로직 없음

⭐ 도입 효과

- 변경에 유연한 구조: 도메인 규칙 변화가 발생해도 인프라 코드와 격리됨
- 테스트 용이성 향상: 비즈니스 로직을 mock 없는 순수 단위 테스트로 검증 가능
- 이벤트 주도 확장 용이: 도메인 이벤트가 명확해지면서 서비스 간 연동 구조가 자연스럽게 확장됨
- MSA 친화적 구조: 도메인이 서비스의 중심이 되기 때문에 확장 시 결합도가 낮음

## 이벤트 흐름 상세(Event Flow)

### OrderCreated → StockDecrease 처리

- Listener: `OrderCreatedRabbitListener`
- 메시지 → DTO 변환 → Handler 호출
- Handler 내부 로직:
  ```
  decrease() {
      load stock;
      optimistic lock protect;
      enforce idempotency;
      save history;
      publish StockDecreasedEvent;
  }
  ```

### StockDecreasedEvent 발행

- Topic Exchange 로 전파
- Routing Key: `stock.decreased`
- Payload 예:
    - orderId
    - productVariantId
    - decreasedQuantity
    - historyId

## 에러 처리 전략(Error Handling Strategy)

- `StockApplicationException` 기반 중앙 집중식 예외 처리
- Feign 오류 → `ProductFeignErrorDecoder` 를 통해 도메인 예외로 변환
- `GlobalErrorCode` 기반의 일관된 응답 포맷 유지

## 테스트 전략(Testing Strategy)

- ApplicationEvents 기반 Domain Event 검증
- SpringBootTest 기반 통합 테스트 구성
## RabbitMQ 큐 설정 설정(Configuration)

### application.yml 예시

```yaml
rabbit:
  stock:
    exchange: stock.exchange
    registered:
      routing-key: stock.registered
      queue: stock.registered.queue
    decreased:
      routing-key: stock.decreased
      queue: stock.decreased.queue
    decrease-fail:
      routing-key: stock.decrease.fail
      queue: stock.decreased.fail.queue
```

- 단순히 엔티티 종류나 기능별로 큐를 쪼갠 것이 아니라, **“어떤 비즈니스 이벤트가 발생했는가?”**를 중심축으로 큐 구조를 설계

> stock.registered — 재고가 등록될 때 발생하는 이벤트 <br>
> stock.decreased — 정상적으로 재고가 감소했을 때 발생하는 이벤트 <br>
> stock.decrease.fail — 재고 감소가 실패했을 때 발행되는 보상/실패 이벤트
