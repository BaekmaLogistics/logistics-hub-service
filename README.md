# 🏭 Hub Service

> Baekma Logistics의 **허브, 허브 경로 및 허브 재고 도메인**을 담당하는 Microservice입니다.

---

## 📌 담당 기능

- 허브 CRUD 및 담당자 관리
- 허브 간 이동 경로 CRUD
- Dijkstra 기반 허브 간 최적 경로 탐색
- 허브별 상품 재고 및 안전 재고 관리
- 주문에 따른 재고 차감 및 복구
- Naver Maps API를 활용한 Geocoding 및 이동 경로 정보 조회
- OpenFeign을 통한 서비스 간 동기 통신
- RabbitMQ를 통한 이벤트 기반 비동기 통신

---

## 🛠 Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-59666C?logo=hibernate&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?logoColor=white)

### Database / Cache

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)
![Redisson](https://img.shields.io/badge/Redisson-DC382D?logo=redis&logoColor=white)

### Communication / Messaging

![OpenFeign](https://img.shields.io/badge/OpenFeign-6DB33F?logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?logo=rabbitmq&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka-6DB33F?logo=spring&logoColor=white)
![Resilience4j](https://img.shields.io/badge/Resilience4j-6DB33F?logo=spring&logoColor=white)

### Observability

![Zipkin](https://img.shields.io/badge/Zipkin-000000?logo=apache&logoColor=white)

---

## ✨ 주요 구현 내용

### 1. 허브 및 허브 간 이동 경로 관리

- 허브 및 허브 간 이동 경로 CRUD 구현
- Naver Maps API를 활용하여 주소 기반 좌표 변환 및 허브 간 거리·소요 시간 계산
- Soft Delete를 적용하여 삭제 데이터 이력 관리

### 2. 최적 경로 탐색 및 캐싱

- 허브 간 이동 정보를 Graph로 구성하고 Dijkstra 알고리즘을 이용한 최적 경로 탐색 구현
- Redis Cache-Aside를 적용하여 반복되는 최단 경로 조회 결과 캐싱
- 허브 경로 변경 시 RabbitMQ 이벤트를 통해 Graph 및 캐시 갱신

### 3. 허브 재고 동시성 및 멱등성 처리

- 허브별 상품 재고 및 안전 재고 관리
- Redisson Distributed Lock을 적용하여 동시에 발생하는 재고 차감 요청 제어
- 요청 식별자를 기반으로 동일 재고 요청의 중복 처리 방지
- 재고 차감 및 복구를 위한 내부 API 구현

### 4. MSA 서비스 간 통신 안정화

- OpenFeign을 활용한 Product / User Service 동기 통신
- Eureka Service Discovery를 통한 서비스 이름 기반 호출
- Resilience4j Circuit Breaker를 적용하여 타 서비스 장애 전파 방지
- RabbitMQ를 활용한 도메인 변경 이벤트 비동기 처리

---

## 💡 기술적 고민 및 회고

### JVM In-Memory Graph와 분산 환경의 일관성 Trade-off

**초기 선택**

최단 경로 계산마다 DB에서 허브와 이동 경로를 조회하는 비용을 줄이기 위해, Graph를 **JVM Local Memory**에 저장하고 Dijkstra 알고리즘을 수행하도록 설계했습니다.

```text
DB → JVM In-Memory Graph → Dijkstra → Redis 최단 경로 캐시
```
고민한 점

Scale-out 환경에서는 각 인스턴스가 독립적인 Graph를 가지기 때문에, 이벤트 처리 실패나 인스턴스 재시작 시 서로 다른 버전의 Graph를 사용할 가능성이 있었습니다.

```declarative
Instance A → Graph v10
Instance B → Graph v10
Instance C → Graph v9
```


이를 해결하기 위해 버전 관리, 주기적 동기화 등을 추가할수록 Local Cache의 성능 이점에 비해 분산 상태를 관리하는 복잡도가 커지는 문제가 발생했습니다.

회고

현재 약 17개의 허브와 낮은 경로 변경 빈도를 고려했을 때, 수 ms의 성능 향상을 위해 복잡한 분산 동기화 구조를 추가하는 것은 과도한 최적화가 될 수 있다고 판단했습니다.

향후 Scale-out이 필요하다면 다음과 같이 역할을 분리하여 JVM 상태에 의존하지 않고 복구 가능한 구조를 우선 검토할 수 있습니다.
```declarative
RabbitMQ → 변경 알림
Redis/DB → 최신 상태의 기준
JVM → 언제든 재생성 가능한 복사본
```


이번 경험을 통해 기술 선택 시 성능만 비교하는 것이 아니라 얻는 이점과 일관성·장애 복구·운영 복잡성의 비용을 함께 고려해야 한다는 점을 느꼈습니다.

---
## 🚀 실행 방법

```bash
./gradlew bootRun
```

## ⚙️ 환경 변수
```declarative
# =========================
# PostgreSQL
# =========================
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=

# =========================
# Redis
# =========================
REDIS_HOST=
REDIS_PORT=

# =========================
# RabbitMQ
# =========================
RABBITMQ_HOST=
RABBITMQ_PORT=
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=

# =========================
# Zipkin
# =========================
ZIPKIN_ENDPOINT=

# =========================
# Naver
# =========================
NAVER_MAP_CLIENT_ID=
NAVER_MAP_CLIENT_SECRET=
NAVER_MAP_URL=
```
---

## 🔗 Project

전체 프로젝트의 아키텍처, ERD, 서비스 구성 및 팀원 역할은 Organization README에서 확인할 수 있습니다.

👉 [Baekma Logistics](https://github.com/BaekmaLogistics)