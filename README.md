# Object-Oriented Store

Java, JDBC, MySQL을 이용해 구현한 콘솔 기반 온라인 편의점 팀 프로젝트입니다.

멤버십 가입과 로그인부터 상품 조회, 주문, 행사 할인, 결제, 포인트 적립 및 결제 취소까지 편의점 구매의 주요 흐름을 구현했습니다.

---

## 프로젝트 목표

- Java 객체지향 프로그래밍을 활용한 편의점 시스템 구현
- 상품, 행사, 멤버십, 주문, 결제 도메인의 책임 분리
- View, Controller, Service, Repository, Model 계층 적용
- JDBC를 이용한 MySQL CRUD 구현
- ERD 기반 PK/FK 및 데이터 관계 설계
- 트랜잭션을 활용한 주문·결제 데이터 일관성 유지
- Git 브랜치와 Pull Request를 활용한 팀 협업

---

## 기술 스택

- Java
- Gradle
- MySQL 8
- JDBC
- MySQL Connector/J
- IntelliJ IDEA
- Git / GitHub

---

## 아키텍처

```text
View
  ↓
Controller
  ↓
Service
  ↓
Repository(DAO)
  ↓
MySQL
```

### 패키지 구조

```text
com.ohgiraffers.store
├── maincontroller
├── common
├── category
├── product
├── promotion
├── member
├── order
└── payment
```

도메인별로 필요한 다음 계층을 구성했습니다.

```text
controller
service
repository
model
view
```

---

## 주요 사용자

### 멤버십

- 멤버십 가입 및 로그인
- 멤버십 정보 조회·수정·탈퇴
- 카테고리별 상품 조회
- 상품 주문
- 주문 상품 조회·수정·삭제
- 결제 방식 선택
- 포인트 결제
- 결제 내역 조회
- 결제 취소
- 멤버십 등급에 따른 포인트 적립

### 관리자

- 상품 등록·조회·수정·삭제
- 카테고리별 상품 관리
- 행사 등록·조회·수정·삭제
- 행사 대상 상품 등록
- 멤버십 등급 및 포인트 적립률 관리

---

## 주요 기능

### 1. 멤버십 관리

- 멤버십 가입 및 로그인
- 멤버십 정보 조회·수정·탈퇴
- 관리자와 일반 멤버십 화면 분리
- 멤버십별 보유 포인트 관리
- 누적 구매금액 관리
- 누적 구매금액에 따른 멤버십 등급 변경
- 멤버십 등급에 따른 포인트 적립

### 2. 상품 관리

- 상품 등록·조회·검색·수정
- 카테고리별 상품 조회
- 상품 논리 삭제
- 재고수량에 따른 판매 가능 여부 확인
- 결제 완료 시 구매 수량만큼 재고 차감
- 결제 취소 시 구매 수량만큼 재고 복구

상품 삭제 시 실제 데이터를 제거하지 않고 `is_deleted` 값을 변경하여 조회 대상에서 제외합니다.

### 3. 행사 관리

- 진행 중인 행사 조회
- 행사 등록·수정·삭제
- 행사 할인율 관리
- 행사 진행 상태 관리
- 행사 대상 상품 등록
- 행사와 상품의 다대다 관계 관리

활성 상태가 `Y`인 행사만 주문 할인에 적용합니다. 하나의 상품에 여러 행사가 연결된 경우 가장 높은 할인율을 적용합니다.

### 4. 주문 관리

- 멤버십별 `PENDING` 주문 생성
- 주문 상품 추가
- 동일 상품 재추가 시 기존 수량 증가
- 주문 상품 목록 조회
- 주문 상품 수량 수정
- 주문 상품 일부 삭제
- 주문 상품 전체 삭제
- 주문 상품 변경 시 주문 금액 재계산
- 활성 행사 할인 금액 자동 계산
- 결제 완료 시 주문 상태를 `PAID`로 변경
- 결제 취소 시 주문 상태를 `CANCELED`로 변경

주문 금액은 다음과 같이 관리합니다.

```text
할인 전 금액 = 상품 가격 × 주문 수량의 합계
할인 금액 = 상품별 활성 행사 할인 금액의 합계
최종 금액 = 할인 전 금액 - 할인 금액
```

### 5. 결제 관리

지원하는 결제 방식:

- 카드
- 카카오페이
- 휴대폰 결제
- 포인트 결제

결제 기능:

- 결제 전 주문 금액 확인
- 행사 할인 금액 반영
- 포인트 전액 결제
- 포인트 부족 시 다른 결제 방식 재선택
- 결제 내역 저장
- 멤버십별 결제 내역 전체 조회
- 결제번호를 이용한 상세 조회
- 완료된 결제 취소
- 결제 완료 시 상품 재고 차감
- 결제 취소 시 상품 재고 복구
- 결제 취소 시 사용 포인트 복구
- 결제로 적립된 포인트 회수
- 누적 구매금액 갱신

---

## 구매 흐름

```text
멤버십 로그인
    ↓
카테고리 선택
    ↓
상품 선택 및 수량 입력
    ↓
PENDING 주문 생성
    ↓
주문 상품 추가·수정·삭제
    ↓
상품 금액 및 행사 할인 재계산
    ↓
결제 방식 선택
    ↓
결제 등록
    ↓
재고 차감
    ↓
포인트 차감 또는 적립
    ↓
주문 상태 PAID
    ↓
결제 상태 COMPLETED
```

### 결제 취소 흐름

```text
결제 내역 선택
    ↓
COMPLETED 결제 여부 확인
    ↓
결제 상태 CANCELED
    ↓
주문 상태 CANCELED
    ↓
사용 포인트 복구
    ↓
적립 포인트 회수
    ↓
누적 구매금액 차감
    ↓
상품 재고 복구
```

결제 등록과 취소 과정은 JDBC 트랜잭션으로 처리합니다. 중간 과정에서 하나라도 실패하면 전체 작업을 롤백합니다.

---

## 주문 및 결제 상태

### 주문 상태

| 상태 | 의미 |
|---|---|
| `PENDING` | 결제 전 주문 |
| `PAID` | 결제가 완료된 주문 |
| `CANCELED` | 결제가 취소된 주문 |

### 결제 상태

| 상태 | 의미 |
|---|---|
| `PENDING` | 결제 처리 전 |
| `COMPLETED` | 결제 완료 |
| `CANCELED` | 결제 취소 |
| `FAILED` | 결제 실패 |

---

## 핵심 도메인

| 도메인 | 역할                                           |
|---|------------------------------------------------|
| Category | 상품 카테고리 관리                             |
| Product | 상품 정보, 가격, 재고 및 삭제 상태 관리        |
| Promotion | 행사 정보, 할인율 및 진행 상태 관리            |
| PromotionProduct | 행사와 상품의 연결 관계 관리                   |
| Member | 멤버십 계정, 보유 포인트 및 누적 구매금액 관리 |
| MembershipGrade | 멤버십 등급과 포인트 적립률 관리               |
| Order | 주문 금액과 주문 상태 관리                     |
| OrderItem | 주문에 포함된 상품과 수량 관리                 |
| Payment | 결제 방식, 결제 금액 및 결제 상태 관리         |

---

## 데이터 관계

```text
MembershipGrade 1 ── N Member
Member          1 ── N Order
Order           1 ── N OrderItem
Product         1 ── N OrderItem
Order           1 ── 1 Payment
Category        1 ── N Product
Promotion       N ── M Product
```

행사와 상품의 다대다 관계는 `tbl_promotion_product`가 연결합니다.

---

## 데이터베이스 설정

### 1. 데이터베이스 생성

MySQL에서 다음 데이터베이스를 생성합니다.

```sql
CREATE DATABASE object_oriented_store
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
```

### 2. SQL 스크립트 실행

`database` 폴더의 SQL 파일을 실행합니다.

새로운 데이터베이스를 구성할 때의 실행 순서:

```text
01_create_tables.sql
02_insert_seed_data.sql
03_insert_test_data.sql
```

| 파일 | 설명                                                  |
|---|-------------------------------------------------------|
| `01_create_tables.sql` | 테이블과 제약조건 생성                                |
| `02_insert_seed_data.sql` | 멤버십 등급과 카테고리 기본 데이터 등록               |
| `03_insert_test_data.sql` | 개발용 멤버십, 상품, 행사 및 주문 데이터 등록         |
| `04_remove_product_status.sql` | 기존 DB의 `product_status` 컬럼 제거용 마이그레이션   |
| `05_add_product_soft_delete.sql` | 기존 DB에 상품 논리 삭제 컬럼을 추가하는 마이그레이션 |

`04`, `05` 스크립트는 기존 데이터베이스를 최신 구조로 변경할 때 사용하는 마이그레이션입니다. 최신 `01_create_tables.sql`로 새 DB를 만든 경우에는 별도로 실행하지 않아도 됩니다.

### 3. DB 접속 정보 작성

다음 파일을 생성합니다.

```text
src/main/resources/database.properties
```

파일 내용:

```properties
db.url=jdbc:mysql://localhost:3306/object_oriented_store
db.user=oodbms
db.password=본인의비밀번호
```

`database.properties`는 로컬 DB 접속 정보를 포함하므로 Git에 커밋하지 않습니다.

---

## 테스트 계정

`03_insert_test_data.sql`을 실행하면 다음 개발용 계정이 생성됩니다.

| 구분   | 아이디 | 비밀번호 |
|--------|---|---|
| BASIC  | `basic_member` | `test1234` |
| GOLD   | `gold_member` | `test1234` |
| VIP    | `vip_member` | `test1234` |
| 관리자 | `admin` | `admin1234` |

테스트 전용 계정이며 실제 서비스 계정으로 사용하지 않습니다.

---

## 실행 방법

1. MySQL 데이터베이스를 생성합니다.
2. SQL 스크립트를 순서대로 실행합니다.
3. `database.properties`에 로컬 DB 접속 정보를 입력합니다.
4. IntelliJ에서 Gradle 프로젝트를 불러옵니다.
5. 다음 클래스의 `main()` 메서드를 실행합니다.

```text
com.ohgiraffers.store.maincontroller.MainRun
```

Gradle로 빌드 상태를 확인할 수도 있습니다.

### Git Bash

```bash
./gradlew build
```

### Windows 명령 프롬프트

```cmd
gradlew.bat build
```

---

## 협업 방식

- 도메인별 담당 기능 분리
- 기능별 브랜치에서 작업
- 작업 단위별 커밋
- Pull Request를 통한 코드 검토 및 병합
- `main` 최신화 후 기능 브랜치에 병합
- 요구사항과 ERD 변경 내용 공유

---

## 향후 개선 사항

- 중복 행사 적용 정책 확장
- 단위 테스트 및 통합 테스트 보완
- 사용자 입력값 검증 강화
- 예외 메시지 세분화
- 민감한 멤버십 정보 암호화
