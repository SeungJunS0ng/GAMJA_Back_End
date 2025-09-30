# 게시판 프로젝트 (Board Application)

Spring Boot 기반의 웹 게시판 애플리케이션입니다.

## 📋 프로젝트 개요

이 프로젝트는 Spring Boot, JPA, Thymeleaf를 사용하여 구현된 게시판 시스템입니다. 
게시물 작성, 수정, 삭제, 조회 및 파일 업로드/다운로드 기능을 제공합니다.

## 🛠️ 기술 스택

- **Backend**: Spring Boot 2.5.4, Spring Data JPA, Spring Validation
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: MariaDB, H2 (테스트용)
- **Build Tool**: Gradle
- **Java Version**: 1.8

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/study/board/
│   │   ├── controller/      # 웹 컨트롤러
│   │   ├── service/         # 비즈니스 로직
│   │   ├── repository/      # 데이터 접근 계층
│   │   ├── entity/          # JPA 엔티티
│   │   ├── dto/             # 데이터 전송 객체
│   │   ├── exception/       # 예외 처리
│   │   ├── util/            # 유틸리티 클래스
│   │   └── aws/             # AWS S3 관련 (주석 처리됨)
│   └── resources/
│       ├── templates/       # Thymeleaf 템플릿
│       ├── static/          # 정적 리소스
│       └── application.yml  # 설정 파일
└── test/                    # 테스트 코드
```

## ✨ 주요 기능

### 게시물 관리
- ✅ 게시물 작성, 수정, 삭제
- ✅ 게시물 목록 조회 (페이징 처리)
- ✅ 게시물 상세 조회
- ✅ 제목 기반 검색 기능

### 파일 관리
- ✅ 파일 업로드 (10MB 제한)
- ✅ 파일 다운로드
- ✅ 허용 파일 형식 제한
- ✅ 파일 크기 및 형식 검증

### 보안 및 검증
- ✅ Bean Validation을 통한 입력 검증
- ✅ 파일 업로드 보안 검증
- ✅ 전역 예외 처리
- ✅ XSS 방지 (Thymeleaf 자동 이스케이프)

### 기타 기능
- ✅ 생성일시/수정일시 자동 관리
- ✅ 페이징 및 정렬
- ✅ 응답형 UI
- ✅ 로깅 시스템

## 🚀 실행 방법

### 1. 사전 요구사항
- Java 8 이상
- MariaDB 10.x
- Gradle

### 2. 데이터베이스 설정
MariaDB에 `board` 데이터베이스를 생성합니다.
```sql
CREATE DATABASE board CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 애플리케이션 설정
`src/main/resources/application.yml` 파일에서 데이터베이스 접속 정보를 수정합니다.
```yaml
spring:
  datasource:
    username: your_username
    password: your_password
    url: jdbc:mariadb://localhost:3306/board
```

### 4. 애플리케이션 실행
```bash
# Windows
.\gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

### 5. 접속
브라우저에서 `http://localhost:8080`에 접속합니다.

## 🧪 테스트 실행

```bash
# 모든 테스트 실행
.\gradlew test

# 특정 테스트 클래스 실행
.\gradlew test --tests BoardServiceTest
```

## 📝 API 엔드포인트

| HTTP Method | URI | 설명 |
|-------------|-----|------|
| GET | `/` | 메인 페이지 (게시물 목록으로 리다이렉트) |
| GET | `/board/list` | 게시물 목록 조회 |
| GET | `/board/write` | 게시물 작성 폼 |
| POST | `/board/writepro` | 게시물 작성 처리 |
| GET | `/board/view?id={id}` | 게시물 상세 조회 |
| GET | `/board/modify/{id}` | 게시물 수정 폼 |
| POST | `/board/update/{id}` | 게시물 수정 처리 |
| GET | `/board/delete?id={id}` | 게시물 삭제 |
| GET | `/board/download/{id}` | 파일 다운로드 |

## 📋 데이터베이스 스키마

### Board 테이블
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | INTEGER | PK, AUTO_INCREMENT | 게시물 ID |
| title | VARCHAR(200) | NOT NULL | 제목 |
| content | VARCHAR(4000) | NOT NULL | 내용 |
| filename | VARCHAR(255) | NULL | 첨부 파일명 |
| filepath | VARCHAR(255) | NULL | 파일 경로 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

## 🔧 설정 및 환경변수

### application.yml 주요 설정
- 서버 포트: 8080
- 파일 업로드 최대 크기: 10MB
- JPA DDL 모드: update (자동 테이블 생성/수정)
- 로깅 레벨: DEBUG (개발용)

### 파일 업로드 제한사항
- 최대 파일 크기: 10MB
- 허용 파일 형식: jpg, jpeg, png, gif, pdf, doc, docx, txt, zip, rar

## 🐛 트러블슈팅

### 일반적인 문제들

1. **데이터베이스 연결 실패**
   - MariaDB 서비스가 실행 중인지 확인
   - 데이터베이스 접속 정보 확인
   - 방화벽 설정 확인

2. **파일 업로드 실패**
   - 파일 크기가 10MB를 초과하지 않는지 확인
   - 허용된 파일 형식인지 확인
   - 파일 저장 디렉토리 권한 확인

3. **페이지 로딩 오류**
   - 브라우저 캐시 삭제
   - 애플리케이션 재시작

## 🚀 향후 개선 계획

- [ ] Spring Security 적용 (사용자 인증/권한)
- [ ] REST API 추가
- [ ] AWS S3 파일 업로드 연동
- [ ] 댓글 시스템
- [ ] 게시물 카테고리 기능
- [ ] 이메일 알림 기능
- [ ] 게시물 좋아요/조회수 기능

## 📄 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다.

## 👥 기여

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 생성해 주세요.
