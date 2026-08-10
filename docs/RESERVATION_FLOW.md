## 예약 생성 흐름

```mermaid
sequenceDiagram
		participant Client as Client
		participant IT as IdempontencyInterceptor
		participant IS as IdempontencyRedisService
		participant RC as ReservationController
    participant RS as ReservationService
    participant RP as ReservationProcessor
    participant RT as ReservationTransactionService
    participant DB as Database
    
    rect rgb(255, 249, 196)
    Note over Client, DB: Case 1: 정상 동작
    end
    IT->>IS: tryProcessing(RESERVATION, key, userId, requestHash)
    IT->>RC: true 반환(선점성공)
    RC->>RS: 예약 요청
    RS->>RP: (방어)재시도메서드
    RP->>RT: 예약생성 트랜잭션 호출
    RT->>DB: 재고조회
    RT->>DB: 예약생성
    note over RT: 트랜잭션 커밋
    RS->>IS: Redis 예약키 상태변경(processing -> completed)
    RS-->>Client: 201 Created
    
    rect rgb(255, 249, 196)
    Note over Client, DB: Case 2: 충돌 발생
    end
    
    RP->>RT: 예약생성 트랜잭션 호출
    RT->>DB: 재고 차감 시도
    alt OptimisticLockException
        DB-->>RP: 충돌 발생
        RP->>RP: 100ms 대기 후 재시도 (최대 3회)
    else 성공
		    RP->>RT: reservation 저장 시도
		    RT->>DB: 재고 차감 시도
        note over RT: 트랜잭션 커밋
        RP->>IS: Redis 예약키 상태변경(processing -> completed)
        RS-->>Client: 201 Created
    else 재시도 모두 실패
		    RS->>IS: Redis 예약키 상태변경(processing -> failed)
        RS-->>Client: 409 Conflict (RESERVATION_CONFLICT)
    end

```

## 재시도 및 트랜잭션 분리 아키텍처

| 구분 | 주요 수행 작업 | 트랜잭션 / 데이터 동작 | 분리 사유 및 핵심 효과 |
| :--- | :--- | :--- | :--- |
| **ReservationProcessor**<br>*(재시도 제어)* | • 백오프 재시도 제어<br>*(100ms 대기, 최대 3회)* | **`[트랜잭션 없음]`**<br>In-Memory 예외 수집 및 재시도 루프 수행 | 모든 재시도가 한 `@Transactional` 내부에서 돌아가는 것을 방지 |
| **ReservationTransaction Service**<br>*(단일 시도)* | • 재고 조회 및 차감<br>• 예약 생성 및 저장 | **`[@Transactional 수행]`** | 낙관적 락(`OptimisticLockException`) 발생 시 해당 시도의 단일 트랜잭션만 독립적으로 Rollback.<br>매 재시도마다 새로운 DB 커넥션을 얻어 최신 DB 재고 상태 조회 |


## 주요 클래스

```mermaid
classDiagram
    class ReservationProcessor {
        + processWithRetry(ReservationRequest, Long): void
        + recover(ObjectOptimisticLockingFailureException, ReservationRequest, Long): void
        + recover(CustomException, ReservationRequest, Long): void
    }

    class ReservationService {
        + createReservation(ReservationRequest, Long, String): ReservationCreateResponse
    }

    class ReservationTransactionService {
        + createReservationInTransaction(ReservationRequest, Long): String
    }

    class IdempotencyRedisService {
        + tryProcessing(IdempotencyDomain, String, Long, String): boolean
        + get(IdempotencyDomain, String): Optional~IdempotencyValue~
        + complete(IdempotencyDomain, String): void
        + fail(IdempotencyDomain, String): void
    }

    ReservationService --> ReservationProcessor
    ReservationProcessor --> ReservationTransactionService
    ReservationService --> IdempotencyRedisService
```
