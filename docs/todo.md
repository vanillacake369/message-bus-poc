# 메시지 브로커 검증 작업

이 문서는 주요 영역별 메시지 브로커 기능을 평가하기 위한 **체계적인 검증 작업**을 정리한 것입니다.

---

**마지막 업데이트:** 2025-07-29
**다음 검토 일정:** 주간 스프린트 플래닝 세션

---

## 🔥 **우선순위 높은 작업 (High Priority Tasks)**

### 🔧 구현 작업 (Implementation Tasks)

#### 1. OpenTelemetry (OTLP) 메트릭 구현

**Phase 1A: 공통 OTLP 구성**
- [x] **shared 모듈에 OTLP 설정 추가**
  - [x] OpenTelemetry SDK 의존성 추가
  - [x] OTLP Exporter 설정 클래스 생성
  - [x] 공통 메트릭 태그 및 리소스 속성 정의
  - [x] 서비스별 메트릭 네이밍 컨벤션 설정
  - [ ] OTLP Prometheus / Grafana 구성 

**Phase 1B: Kafka OTLP 메트릭 구현**
- [ ] **inventory-service OTLP 메트릭 설정**
  - [ ] OpenTelemetry 의존성 추가 및 기존 Micrometer 대체
  - [ ] Kafka 메시지 처리 메트릭을 OTLP 형식으로 변환
  - [ ] 메트릭 수집기(Meter) 설정 및 gauge/counter/histogram 구현
  - [ ] OTLP 엔드포인트 설정 (http://otel-collector:4318/v1/metrics)

**Phase 1C: RabbitMQ OTLP 메트릭 구현**
- [ ] **notification-service OTLP 메트릭 설정**
  - [ ] OpenTelemetry 의존성 추가
  - [ ] RabbitMQ 메시지 처리 메트릭을 OTLP 형식으로 구현
  - [ ] AMQP 연결 상태 메트릭 추가
  - [ ] OTLP 배치 전송 설정 최적화

**Phase 1D: Pulsar OTLP 메트릭 구현**
- [ ] **analytics-service OTLP 메트릭 설정**
  - [ ] OpenTelemetry 의존성 추가
  - [ ] Pulsar 메시지 처리 메트릭을 OTLP 형식으로 구현
  - [ ] 구독별 백로그 메트릭 추가
  - [ ] OTLP 메트릭 스키마 검증

**Phase 1E: OTLP 인프라 구성**
- [ ] **OpenTelemetry Collector 설정**
  - [ ] docker-compose.yml에 otel-collector 서비스 추가
  - [ ] OTLP 수신기 및 Prometheus exporter 설정
  - [ ] 메트릭 파이프라인 및 배치 처리 구성
  - [ ] Grafana 데이터소스를 Prometheus에서 OTLP로 변경

- [ ] **Grafana 대시보드 업데이트**
  - [ ] OTLP 메트릭 형식에 맞춘 쿼리 변경
  - [ ] 실시간 TPS/지연시간 대시보드 업데이트
  - [ ] 브로커별 성능 비교 패널 OTLP 형식 적용
  - [ ] 컨슈머 lag 및 처리 속도 OTLP 메트릭 시각화

- [ ] **구조화된 로깅 개선**
  - [ ] OpenTelemetry 로그 correlation 추가
  - [ ] Trace ID 및 Span ID를 로그에 포함
  - [ ] OTLP 로그 export 설정 (선택사항)
  - [ ] 로그-메트릭 연동을 통한 디버깅 향상

#### 2. Kafka 전용 API 구현
- [ ] **Kafka Admin API 통합**
  - [ ] 토픽 생성/삭제/설정 변경 REST API
  - [ ] 파티션 리밸런싱 API 엔드포인트
  - [ ] 컨슈머 그룹 관리 (reset offset, describe groups)
  - [ ] 토픽별 메시지 통계 조회 API

- [ ] **고급 프로듀서 기능**
  - [ ] 트랜잭션 메시지 발송 API
  - [ ] 배치 메시지 처리 최적화
  - [ ] 압축 알고리즘별 성능 테스트 (snappy, lz4, gzip)
  - [ ] 메시지 키 기반 파티션 라우팅 전략

- [ ] **컨슈머 제어 API**
  - [ ] 동적 컨슈머 pause/resume 제어
  - [ ] 수동 오프셋 커밋 관리
  - [ ] 컨슈머 지연(lag) 실시간 모니터링
  - [ ] 리밸런싱 리스너 및 상태 추적

#### 3. Kafka 적용
- [ ] **Spring Kafka 고급 설정**
  - [ ] ErrorHandler 및 RetryTemplate 커스터마이징
  - [ ] 컨슈머 동시성 및 스레드 풀 최적화
  - [ ] Kafka Streams 연동 (실시간 집계 처리)
  - [ ] 스키마 레지스트리 연동 (Avro/JSON Schema)

- [ ] **성능 최적화**
  - [ ] 프로듀서 배치 사이즈 및 linger.ms 튜닝
  - [ ] 컨슈머 fetch.min.bytes 및 max.poll.records 조정
  - [ ] 압축 타입별 CPU/네트워크 사용량 비교
  - [ ] 파티션 수 최적화 (CPU 코어 수 기반)

- [ ] **장애 복구 시나리오**
  - [ ] 브로커 노드 장애 시 자동 failover 테스트
  - [ ] 네트워크 분할 시 메시지 중복 처리 방지
  - [ ] 컨슈머 리밸런싱 중 메시지 손실 방지
  - [ ] 오프셋 커밋 전략별 내구성 비교

#### 4. Pulsar 전용 API 구현
- [ ] **Pulsar Admin API 통합**
  - [ ] 네임스페이스/테넌트 관리 REST API
  - [ ] 토픽 파티션 및 구독 관리
  - [ ] 메시지 TTL 및 보존 정책 설정
  - [ ] 백로그 쿼터 및 디스크 사용량 모니터링

- [ ] **고급 메시징 패턴**
  - [ ] 지연 메시지 스케줄링 API
  - [ ] 메시지 압축/압축해제 옵션
  - [ ] 배치 메시지 처리 및 청킹
  - [ ] 메시지 중복 제거(deduplication) 설정

- [ ] **스키마 레지스트리 활용**
  - [ ] 동적 스키마 진화 API
  - [ ] 호환성 검사 및 버전 관리
  - [ ] JSON/Avro/Protobuf 스키마 지원
  - [ ] 스키마 유효성 검사 및 에러 처리

#### 5. Pulsar 적용
- [ ] **구독 모델 최적화**
  - [ ] Exclusive/Shared/Failover/KeyShared 구독 타입 비교
  - [ ] 컨슈머 우선순위 및 가중치 설정
  - [ ] 메시지 순서 보장을 위한 KeyShared 구독 활용
  - [ ] 구독별 백로그 모니터링 및 알람

- [ ] **성능 튜닝**
  - [ ] receiverQueueSize 및 maxTotalReceiverQueueSizeAcrossPartitions 조정
  - [ ] 배치 처리 크기 및 타임아웃 최적화
  - [ ] 메모리 및 디스크 계층 저장소 활용
  - [ ] 압축 알고리즘별 성능 벤치마크

- [ ] **멀티 테넌시 구성**
  - [ ] 테넌트별 리소스 격리 설정
  - [ ] 네임스페이스 기반 액세스 제어
  - [ ] 클러스터 간 지역 복제 설정
  - [ ] 테넌트별 모니터링 및 할당량 관리

#### 6. RabbitMQ 전용 API 구현
- [ ] **RabbitMQ Management API 통합**
  - [ ] 큐/익스체인지/바인딩 관리 REST API
  - [ ] Virtual Host 및 사용자 권한 관리
  - [ ] 큐 메시지 수 및 처리율 실시간 조회
  - [ ] 클러스터 노드 상태 및 동기화 확인

- [ ] **메시지 라우팅 고급 기능**
  - [ ] 다양한 익스체인지 타입 활용 (direct, fanout, topic, headers)
  - [ ] 동적 큐 생성 및 자동 삭제 설정
  - [ ] 메시지 우선순위 큐 구현
  - [ ] 조건부 라우팅 및 메시지 필터링

- [ ] **신뢰성 보장 메커니즘**
  - [ ] Publisher Confirms 및 Return 처리
  - [ ] Consumer Acknowledgment 전략 구현
  - [ ] Persistent 메시지 설정 및 디스크 동기화
  - [ ] High Availability 큐 구성 (mirrored queues)

#### 7. RabbitMQ 적용
- [ ] **Spring AMQP 고급 설정**
  - [ ] RetryTemplate 및 백오프 전략 커스터마이징
  - [ ] 동적 리스너 관리 및 스케일링
  - [ ] 메시지 변환기 및 직렬화 최적화
  - [ ] Connection Factory 풀링 및 재연결 로직

- [ ] **클러스터링 및 고가용성**
  - [ ] RabbitMQ 클러스터 구성 및 노드 추가/제거
  - [ ] 큐 미러링 정책 및 동기화 전략
  - [ ] 로드 밸런서를 통한 연결 분산
  - [ ] 장애 조치 시나리오 및 복구 시간 측정

- [ ] **메시지 흐름 제어**
  - [ ] QoS 프리페치 설정을 통한 백프레셔 관리
  - [ ] Memory/Disk 알람 임계값 설정
  - [ ] 메시지 TTL 및 자동 만료 정책
  - [ ] 큐 길이 제한 및 오버플로우 처리 (drop-head, reject-publish)

### ✅ 검증 작업 (Verification Tasks)

#### 1. 속도 제어(Rate Control) 구현

* [ ] **디바운스 로직**

  * [ ] Kafka 컨슈머용 Redis 기반 중복 제거 로직 구현
  * [ ] RabbitMQ 컨슈머 측 중복 제거 로직 추가
  * [ ] 설정 가능한 디바운스 윈도우 생성 (기본값: 1초)
  * [ ] 디바운스 메트릭을 Prometheus에 추가

* [ ] **쓰로틀링 메커니즘**

  * [ ] Kafka 컨슈머 `max.poll.records` 및 `pause()/resume()` 설정
  * [ ] RabbitMQ `basicQos(prefetchCount)` 기반 처리 속도 제한
  * [ ] Pulsar `receiverQueueSize`를 통한 백프레셔 설정
  * [ ] 브로커별 쓰로틀링 속도 메트릭 추가

#### 2. 데드 레터 큐(DLQ) 설정

* [ ] **Kafka DLQ**

  * [ ] Spring Kafka에서 `DeadLetterPublishingRecoverer` 구성
  * [ ] `order-events-dlq` 토픽 생성
  * [ ] 재시도 정책 설정 (최대 3회, 지수 백오프)
  * [ ] DLQ 메시지 확인용 API 엔드포인트 추가

* [ ] **RabbitMQ DLQ**

  * [ ] Dead Letter Exchange(`order-events-dlx`) 생성
  * [ ] 기본 큐에 `x-dead-letter-exchange` 설정
  * [ ] TTL 및 최대 재시도 정책 추가
  * [ ] DLQ 모니터링 대시보드 구성

* [ ] **Pulsar DLQ**

  * [ ] `deadLetterPolicy` 구성 및 토픽 네이밍 적용
  * [ ] 최대 재전송 횟수 및 ack 타임아웃 설정
  * [ ] DLQ 전용 구독 추가
  * [ ] Pulsar DLQ 메트릭 수집

#### 3. 컨슈머 우선순위 구현

* [ ] **RabbitMQ 우선순위 컨슈머**

  * [ ] `x-priority` 인자 사용 컨슈머 구현
  * [ ] 고/저 우선순위 컨슈머 인스턴스 생성
  * [ ] 우선순위 기반 메시지 라우팅 로직 추가
  * [ ] 우선순위 처리 효과성 메트릭 추가

* [ ] **Kafka/Pulsar 애플리케이션 레벨 우선순위**

  * [ ] 다중 토픽 기반 우선순위 큐 패턴 설계
  * [ ] 가중치 기반 컨슈머 할당 구현
  * [ ] 우선순위 메시지 헤더 라우팅 추가
  * [ ] 우선순위 처리 메트릭 추가

#### 4. 순서 보장(Ordering) 검증

* [ ] **Kafka 파티션 기반 순서 보장**

  * [ ] 고객 key당 단일 파티션 보장
  * [ ] 순차 처리 검증 로직 구현
  * [ ] 순서 위반 감지 메트릭 추가
  * [ ] 순서 무결성 테스트 작성

* [ ] **RabbitMQ FIFO 큐 순서 보장**

  * [ ] 단일 컨슈머 큐 설정
  * [ ] 메시지 시퀀스 검증 로직 추가
  * [ ] 큐별 순서 메트릭 수집
  * [ ] 다중 컨슈머 환경에서 순서 변화 테스트

* [ ] **Pulsar KeyShared 구독**

  * [ ] KeyShared 구독 모드 설정
  * [ ] key별 순서 검증 로직 추가
  * [ ] key 기반 순서 메트릭 수집
  * [ ] key 분배 효과성 테스트

---

### 📊 테스트 & 검증 작업 (Testing & Validation Tasks)

#### 5. 부하 테스트 시나리오

* [ ] **K6 테스트 스크립트**

  * [ ] 디바운스 테스트 (`k6/debounce-test.js`)
  * [ ] 쓰로틀링 부하 테스트 (`k6/throttle-test.js`)
  * [ ] 우선순위 컨슈머 테스트 (`k6/priority-test.js`)
  * [ ] 순서 검증 테스트 (`k6/ordering-test.js`)

* [ ] **성능 벤치마킹**

  * [ ] 브로커별 TPS(초당 처리량) 측정
  * [ ] 지연 시간 퍼센타일(p50, p95, p99) 기록
  * [ ] 메모리 및 CPU 사용 패턴 모니터링
  * [ ] 성능 비교 매트릭스 작성

#### 6. 관측 및 모니터링

* [ ] **브로커 메트릭 수집**

  * [ ] Kafka 컨슈머 지연 모니터링(JMX)
  * [ ] RabbitMQ 관리 API 메트릭 수집
  * [ ] Pulsar admin stats 기반 모니터링
  * [ ] Grafana에서 통합 메트릭 대시보드 구성

* [ ] **커스텀 애플리케이션 메트릭**

  * [ ] 메시지 처리 시간 측정
  * [ ] 에러율 및 재시도 횟수 추적
  * [ ] 컨슈머 우선순위 처리 효과성 메트릭 추가
  * [ ] 순서 위반 감지 카운터 추가

#### 7. CDC(체인지 데이터 캡처) 통합 테스트

* [ ] **Debezium 설정**

  * [ ] Kafka용 Debezium MySQL 커넥터 구성
  * [ ] RabbitMQ Sink 커넥터 설정
  * [ ] Pulsar IO Debezium 커넥터 구성
  * [ ] 각 브로커별 CDC 이벤트 흐름 테스트

* [ ] **변경 이벤트 검증**

  * [ ] 테스트용 DB 작업 생성
  * [ ] 브로커 토픽에서 변경 이벤트 확인
  * [ ] 브로커별 CDC 지연 시간 측정
  * [ ] CDC 모니터링 메트릭 추가

---

### 🛠️ 인프라 & 자동화 작업 (Infrastructure & Automation)

#### 8. 테스트 자동화 스크립트

* [ ] **DLQ 테스트 스크립트**

  * [ ] `scripts/test-dlq.sh` 작성 (브로커 파라미터 지원)
  * [ ] 실패 시나리오 생성 로직 추가
  * [ ] DLQ 메시지 검증 절차 추가
  * [ ] DLQ 정리(cleanup) 자동화

* [ ] **메트릭 수집 스크립트**

  * [ ] `scripts/collect-metrics.sh` 작성
  * [ ] CSV/JSON으로 메트릭 자동 수출
  * [ ] 비교 리포트 생성 기능 추가
  * [ ] 과거 메트릭 히스토리 관리

#### 9. 검증용 테스트 모듈

* [ ] **verification-tests 모듈 생성**

  * [ ] Gradle 모듈 구조 설정
  * [ ] 브로커 공통 테스트 프레임워크 구현
  * [ ] 브로커별 테스트 설정 추가
  * [ ] 통합 테스트 실행 파이프라인 구성

* [ ] **테스트 데이터 관리**

  * [ ] 테스트 데이터 생성 유틸리티 추가
  * [ ] 테스트 정리(cleanup) 절차 구현
  * [ ] 테스트 격리 메커니즘 추가
  * [ ] 재현 가능한 테스트 시나리오 작성

---

## 📈 **중간 우선순위 작업 (Medium Priority Tasks)**

### 📊 고급 기능 테스트

* [ ] **메시지 라우팅 패턴**

  * [ ] Kafka 토픽 기반 라우팅 구현
  * [ ] RabbitMQ 익스체인지 기반 라우팅 테스트
  * [ ] Pulsar 네임스페이스 기반 라우팅 확인
  * [ ] 성능 및 유연성 비교

* [ ] **스키마 진화 테스트**

  * [ ] Kafka Schema Registry + Avro 스키마 진화 테스트
  * [ ] RabbitMQ JSON 스키마 검증
  * [ ] Pulsar 스키마 레지스트리 호환성 테스트
  * [ ] 스키마 진화 메트릭 추가

#### 11. 복원력 테스트

* [ ] **장애 주입 테스트**

  * [ ] 브로커 노드 장애 시뮬레이션
  * [ ] 컨슈머 장애 조치(failover) 시나리오 검증
  * [ ] 브로커별 복구 시간 측정
  * [ ] 복원력 메트릭 대시보드 추가

* [ ] **네트워크 분할 테스트**

  * [ ] 서비스 간 네트워크 단절 시뮬레이션
  * [ ] 분할 상황에서 메시지 일관성 검증
  * [ ] 브로커별 분할 내성 측정
  * [ ] 복구 절차 문서화

---

## 📋 **문서화 및 보고 (Documentation & Reporting)**

* [ ] **브로커 비교 보고서**

  * [ ] 기능 비교 매트릭스 작성
  * [ ] 성능 벤치마크 결과 문서화
  * [ ] 브로커별 비용 분석 추가
  * [ ] 운영 복잡성 평가 포함

* [ ] **구현 가이드**

  * [ ] 브로커별 설정 가이드 작성
  * [ ] 문제 해결(Troubleshooting) 절차 작성
  * [ ] 베스트 프랙티스 문서화
  * [ ] 마이그레이션 고려사항 추가

---

## 🟢 **낮은 우선순위 작업 (Low Priority Tasks)**

#### 13. 확장 통합

* [ ] **추가 프로토콜 테스트**

  * [ ] WebSocket 연동 테스트
  * [ ] gRPC 스트리밍 통합
  * [ ] HTTP/2 SSE(Server-Sent Events) 지원
  * [ ] 프로토콜 성능 비교

#### 14. 보안 및 컴플라이언스

* [ ] **보안 테스트**

  * [ ] 브로커별 TLS/SSL 구성
  * [ ] 인증 및 권한 부여 검증
  * [ ] 메시지 암호화 테스트
  * [ ] 보안 비교 매트릭스 작성

* [ ] **컴플라이언스 기능**

  * [ ] 메시지 보존 정책 검증
  * [ ] 모든 브로커에 감사 로그(Audit Log) 추가
  * [ ] GDPR 준수 기능(메시지 삭제) 테스트
  * [ ] 컴플라이언스 문서 작성

---

## ✅ **완료 정의 (Success Criteria)**

작업이 완료되었다고 판단하는 기준:

1. **구현**: 기능이 완전하게 동작하며 에러 핸들링 포함
2. **테스트**: 단위/통합 테스트가 통과하고 테스트 커버리지가 80% 이상
3. **문서화**: 구현 내용이 예시와 함께 문서화됨
4. **메트릭**: 관련 메트릭이 Grafana에 표시됨
5. **검증**: 요구사항과 일치하는 동작이 확인됨

---

## 📊 **핵심 성과 지표 (KPI)**

* **처리량**: 모든 브로커가 목표 TPS를 안정적으로 처리
* **지연 시간**: End-to-End 지연 시간이 SLA 기준 만족
* **신뢰성**: DLQ 정책으로 메시지 손실 방지
* **순서 보장**: 동일 key에 대한 순차 처리 유지
* **관측성**: 모든 메트릭이 모니터링 대시보드에서 확인 가능
* **통합성**: CDC 파이프라인이 모든 브로커에서 정상 동작