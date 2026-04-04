# Main Server - README

**자취인 프로젝트 메인 비즈니스 로직 서버**

---

## 개요

Main Server는 자취인 프로젝트의 **핵심 비즈니스 로직**을 담당하는 마이크로서비스입니다.

- **포트**: 8080
- **프레임워크**: Spring Boot 3.1.1
- **언어**: Java 17
- **데이터베이스**: MySQL (JPA/Hibernate)
- **메시지큐**: Kafka (위치 기반 서비스)
- **캐시**: Redis (성능 최적화)
- **주요 기능**:
  - 지도 API (건물/마커 조회)
  - 게시판 API (게시글, 댓글)
  - LLM 기반 추천 (Claude AI)
  - Kafka 기반 위치 이벤트 처리
  - 벡터 검색 (FastAPI 연동)

---

## 프로젝트 구조

```
src/main/
├── java/com/capstone/Jachwi_inServerSpring/
│   ├── controller/
│   │   ├── MapController.java         # 지도 API
│   │   ├── LlmController.java         # LLM 기반 추천
│   │   ├── LocationController.java    # 위치 이벤트
│   │   └── UsersController.java       # 사용자 정보
│   ├── service/
│   │   ├── MapService.java            # 지도 비즈니스 로직
│   │   ├── LlmService.java            # Claude AI 추천
│   │   ├── LocationProducer.java      # Kafka 발행자
│   │   └── PostService.java           # 게시판 비즈니스 로직 (예정)
│   ├── domain/
│   │   ├── Building.java              # 건물 엔티티
│   │   ├── Post.java                  # 게시글 엔티티 (예정)
│   │   ├── User.java                  # 사용자 엔티티
│   │   └── dto/                       # DTO들
│   ├── repository/
│   │   ├── BuildingRepository.java
│   │   ├── PostRepository.java        # 예정
│   │   └── UserRepository.java
│   └── config/
│       ├── KafkaConfig.java
│       ├── CorsConfig.java
│       └── JwtConfig.java             # Auth Server 토큰 검증
├── resources/
│   ├── application.properties         # 설정
│   ├── schema.sql                     # DB 초기화 스크립트
│   ├── data.sql                       # 초기 데이터 (건물 정보)
│   └── kafka/
│       └── topics.properties
└── test/
```

---

## 로컬 환경 설정

### 1. 필수 설치 항목
- Java 17 이상
- MySQL 8.0 이상
- Redis 6.0 이상
- Kafka 3.0 이상 (또는 Docker)
- Gradle 7.x 이상

### 2. 데이터베이스 설정

```bash
# MySQL 실행
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE jachwiin_main;
USE jachwiin_main;

# 테이블 생성
SOURCE src/main/resources/schema.sql;
SOURCE src/main/resources/data.sql;  # 초기 건물 데이터
```

### 3. Redis 실행 (로컬)

```bash
# macOS (Homebrew)
brew install redis
redis-server

# 또는 Docker
docker run -d -p 6379:6379 redis:latest
```

### 4. Kafka 실행 (로컬)

```bash
# Docker Compose 활용 (권장)
docker-compose up -d

# 또는 수동 설치
# https://kafka.apache.org/quickstart
bin/kafka-server-start.sh config/server.properties
```

### 5. 설정 파일 (application.properties)

```properties
server.port=8080

# MySQL 연결
spring.datasource.url=jdbc:mysql://localhost:3306/jachwiin_main
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.topic.location=location-events

# CORS (Flutter 로컬 테스트)
app.cors.allowed-origins=http://localhost:62328
```

### 6. 서버 실행

```bash
./gradlew bootRun

# 또는 JAR 파일 생성 후 실행
./gradlew build
java -jar build/libs/main-server.jar
```

**실행 확인**:
```bash
curl http://localhost:8080/api/v1/map/markers  # 마커 수 조회
```

---

## API 사용 예시

### 1. 지도 범위 내 건물 조회

```bash
curl "http://localhost:8080/api/v1/map/view/position?minX=37.4&maxX=37.5&minY=127.0&maxY=127.1"
```

**응답**:
```json
[
  {
    "id": 1,
    "name": "남숭 하우스",
    "address": "서울시 종로구 ...",
    "latitude": 37.42,
    "longitude": 127.05,
    "price": 450000,
    "floor": 3,
    "features": ["CCTV", "카페 근처"]
  }
]
```

---

### 2. 게시글 자동 분류 (LLM)

```bash
curl -X POST http://localhost:8080/api/v1/llm/classify \
  -H "Content-Type: application/json" \
  -d '{
    "title": "신촌역 근처 룸메이트 구합니다",
    "content": "안녕하세요, 저는 서울대 대학원생이고 ..."
  }'
```

**응답**:
```json
"룸메이트"
```

---

### 3. 자취방 추천 (LLM + 벡터 검색)

```bash
curl -X POST http://localhost:8080/api/v1/llm/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {accessToken}" \
  -d '{
    "budget": 450000,
    "location": "용산역 근처",
    "preferences": "CCTV 많고 카페 가까운 곳"
  }'
```

---

### 4. 위치 이벤트 발행 (Kafka)

```bash
curl -X POST http://localhost:8080/api/v1/location/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {accessToken}" \
  -d '{
    "userId": 1,
    "latitude": 37.42,
    "longitude": 127.05,
    "speed": 0,
    "accuracy": 10.5,
    "timestamp": "2026-04-04T10:30:00Z"
  }'
```

---

## 주요 기능 설명

### 🗺️ 지도 API
- 사각형 범위로 건물 조회 (공간 쿼리)
- Redis 캐시로 성능 최적화
- 마커 수 집계

### 🤖 LLM 기반 추천
- Claude API를 사용한 게시글 자동 분류
- 사용자 조건 기반 자취방 추천
- 임베딩 & 벡터 검색 (FastAPI 연동)

### 📍 실시간 위치 처리
- Kafka 토픽으로 사용자 위치 이벤트 발행
- 위치 기반 서비스의 기반이 됨
- 분석용 데이터 수집

### 💬 게시판 (개발 예정)
- CRUD 기능
- 댓글/대댓글
- 좋아요, 북마크

---

## 필요한 의존성

```gradle
// Spring Web + Data JPA
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

// Database
implementation 'mysql:mysql-connector-java:8.0.28'

// Cache
implementation 'org.springframework.boot:spring-boot-starter-data-redis'

// Kafka
implementation 'org.springframework.kafka:spring-kafka'

// Claude API
implementation 'com.anthropic:anthropic-java:2.17.0'

// Security + JWT (Auth Server 토큰 검증)
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

// Lombok
compileOnly 'org.projectlombok:lombok:1.18.34'
annotationProcessor 'org.projectlombok:lombok:1.18.34'

// Testing
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.kafka:spring-kafka-test'
```

---

## 개발 가이드

### JWT 토큰 검증 (Auth Server 토큰)

```java
// JwtConfig.java
@Configuration
public class JwtConfig {
    
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider("your_jwt_secret_here");
    }
}

// 컨트롤러에서 사용
@PostMapping("/protected-endpoint")
public ResponseEntity<?> protectedEndpoint(
    @RequestHeader("Authorization") String token) {
    
    String extracted = token.replace("Bearer ", "");
    Claims claims = jwtTokenProvider.validateToken(extracted);
    String email = claims.getSubject();
    
    // 비즈니스 로직
    return ResponseEntity.ok("Success");
}
```

### Kafka 메시지 구조

```java
// LocationProducer.java
@Component
public class LocationProducer {
    
    @Value("${spring.kafka.topic.location}")
    private String locationTopic;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendLocation(LocationDto dto) {
        String message = objectMapper.writeValueAsString(dto);
        kafkaTemplate.send(locationTopic, dto.getUserId().toString(), message);
    }
}
```

### FastAPI 연동 (벡터 검색)

```java
// LlmService.java
@Service
public class LlmService {
    
    @Value("${fastapi.base-url}")  // http://localhost:8000
    private String fastApiUrl;
    
    public List<Building> searchSimilarBuildings(String userCondition) {
        // 1. FastAPI /search 호출
        RestTemplate restTemplate = new RestTemplate();
        String url = fastApiUrl + "/search";
        
        // 2. 유사 건물 결과 받기
        // 3. Spring DB에서 상세 정보 조회
        // 4. 결과 반환
        
        return buildings;
    }
}
```

---

## 주의사항 & 보안

⚠️ **프로덕션에서 필수**:

1. **Claude API Key**: 환경변수로 관리 (절대 코드에 하드코딩 금지)
2. **JWT Secret**: Auth Server의 secret과 반드시 일치
3. **MySQL 비밀번호**: 강력한 비밀번호
4. **Redis**: 비밀번호 설정 활성화
5. **Kafka**: SSL/TLS 활성화
6. **CORS**: 신뢰할 수 있는 도메인만 허용

```properties
# 프로덕션 설정
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.kafka.security.protocol=SSL
logging.level.root=WARN
```

---

## Docker를 통한 통합 실행

편의를 위해 `docker-compose.yml`을 생성하면:

```bash
# 모든 서비스 동시 실행
docker-compose up -d

# 서비스별 상태 확인
docker-compose ps

# 서비스 중지
docker-compose down
```

---

## 추가 개발 예정 기능

- [ ] 게시글 CRUD API
- [ ] 댓글/대댓글 API
- [ ] 좋아요 & 북마크 API
- [ ] 게시글 검색 API (Elasticsearch 연동)
- [ ] 실시간 알림 (WebSocket)
- [ ] 메시지 API (1:1 채팅)
- [ ] 사용자 차단 기능
- [ ] 신고 & 관리 API

---

## 문제 해결

### "Connection refused: localhost:3306"
→ MySQL이 실행 중이지 않습니다.

### "Redis 연결 실패"
→ Redis가 실행 중이지 않거나 포트가 다릅니다.

### "Kafka broker not available"
→ Kafka 및 Zookeeper가 실행 중인지 확인하세요.

### "Claude API 인증 실패"
→ `.env` 파일에 `ANTHROPIC_API_KEY` 설정이 있는지 확인하세요.

---

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [MySQL JDBC](https://dev.mysql.com/downloads/connector/j/)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Redis 공식 문서](https://redis.io/documentation)
- [Claude API](https://docs.anthropic.com/)

---

**마지막 업데이트**: 2026-04-04  
**작성자**: 자취인 개발팀
