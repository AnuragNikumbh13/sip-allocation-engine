# Mutual Fund SIP & NAV Allocation Engine

A Spring Boot backend system that manages mutual funds, daily NAV records,
investor SIP mandates, automated SIP execution, NAV-based unit allocation,
and database-enforced idempotency.

## Core Business Formula

For every SIP execution:

```text
Units Allocated = SIP Amount / NAV
```

The calculation is performed using Java `BigDecimal` with a scale of 4
and `RoundingMode.HALF_UP`.

Example:

```text
SIP Amount = ₹5,000
NAV        = ₹247.35

Units = 5000 / 247.35
      = 20.214271...
      = 20.2143 units
```

## 🚀 Key Features

### 1. Mutual Fund Management

The system supports creation and retrieval of mutual fund information.

Each fund contains:

- Fund ID
- Symbol
- Name
- Category
- Active status

## 2. Daily NAV Management

The system stores daily NAV values for each mutual fund.

Each NAV record contains:

- Fund
- NAV date
- NAV price

A database unique constraint is applied to:

```text
(fund_id, nav_date)
```

This ensures that the same fund cannot have multiple NAV records
for the same date.

## 3. Investor SIP Management

Investors can create SIP mandates containing:

- User ID
- Fund
- Monthly SIP amount
- Deduction day
- Active status
- Creation timestamp

## 4. Automated SIP Execution

The scheduled job:

1. Identifies the current date
2. Finds active SIPs whose deduction day matches the current day
3. Checks whether the SIP has already been processed today
4. Looks up the NAV for the same fund and date
5. Calculates units
6. Rounds the result to 4 decimal places
7. Creates a `SIPAllocationRecord`
8. Saves the allocation in PostgreSQL

## 5. Idempotent SIP Processing

The system prevents duplicate SIP execution using:

- Application-level idempotency check
- Database-level unique constraint

```text
(sip_id, allocation_date)
```

## 🏗️ Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Scheduled SIP processing:

```text
Spring Scheduler
      ↓
Find Due SIPs
      ↓
Find Today's NAV
      ↓
Calculate Units
      ↓
Check Idempotency
      ↓
Create SIPAllocationRecord
      ↓
PostgreSQL
```

## 📂 Package Structure

```text
sip_allocation_engine
│
├── controller
├── service
├── scheduler
├── entity
├── repository
├── dto
├── exception
└── config
```

## 🗃️ Domain Model

```text
Fund
 │
 ├── FundNAV
 │
 └── InvestorSIP
        │
        └── SIPAllocationRecord
```

## 🔄 SIP Execution Flow

```text
FUND
  ↓
NAV
  ↓
INVESTOR SIP
  ↓
@Scheduled Job
  ↓
Find Due SIPs
  ↓
Find NAV
  ↓
Amount / NAV = Units
  ↓
BigDecimal(4)
  ↓
Idempotency Check
  /       \
Already    New
Processed
  ↓         ↓
Skip      Allocate
             ↓
      Save Allocation Record
```

## 💰 Financial Calculation

Financial calculations are performed using `BigDecimal`.

```java
BigDecimal unitsAllocated =
    sip.getMonthlyAmount()
       .divide(
           fundNAV.getNavPrice(),
           4,
           RoundingMode.HALF_UP
       );
```

### Why BigDecimal?

Financial calculations require deterministic decimal arithmetic.

The project therefore uses:

```text
BigDecimal
Scale = 4
RoundingMode = HALF_UP
```

## 🌐 REST API

The application exposes 7 HTTP endpoints.

### GET Endpoints

#### 1. Get All Funds

```http
GET /api/v1/funds
```

#### 2. Get Fund by Symbol

```http
GET /api/v1/funds/{symbol}
```

Example:

```http
GET /api/v1/funds/HDFC-FLEXI
```

#### 3. Get All SIPs

```http
GET /api/v1/sips/user
```

Returns all SIP records regardless of user.

#### 4. Get SIPs by User

```http
GET /api/v1/sips/user/{userId}
```

Example:

```http
GET /api/v1/sips/user/101
```

### POST Endpoints

#### 5. Create Fund

```http
POST /api/v1/funds
```

Example:

```json
{
  "symbol": "HDFC-FLEXI",
  "name": "HDFC Flexi Cap Fund",
  "category": "EQUITY",
  "active": true
}
```

#### 6. Create NAV

```http
POST /api/v1/nav
```

Example:

```json
{
  "fundSymbol": "HDFC-FLEXI",
  "navDate": "2026-09-15",
  "navPrice": 247.35
}
```

#### 7. Create SIP

```http
POST /api/v1/sips
```

Example:

```json
{
  "userId": 101,
  "fundSymbol": "HDFC-FLEXI",
  "monthlyAmount": 5000,
  "deductionDay": 15
}
```

## ⏰ Automated SIP Processing

The application uses Spring Scheduling.

During testing:

```java
@Scheduled(cron = "0 * * * * *")
```

For intended daily execution:

```java
@Scheduled(cron = "0 0 9 * * *")
```

The process:

```text
Find active SIP
      ↓
Check deduction day
      ↓
Check existing allocation
      ↓
Find today's NAV
      ↓
Calculate units
      ↓
Create allocation record
      ↓
Persist allocation
```

If today's NAV is unavailable, the SIP execution is skipped.

## 🛡️ Idempotency

A SIP should not be allocated twice for the same date.

The system checks:

```text
SIP + Allocation Date
```

Before creating an allocation.

The database also enforces:

```text
UNIQUE(sip_id, allocation_date)
```

Example:

```text
First execution:
SIP 101 + 2026-09-15
→ Allocation created

Second execution:
SIP 101 + 2026-09-15
→ Already processed
→ Skip
```

## 🗄️ Database

The application uses PostgreSQL.

Database:

```text
sip_allocation
```

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sip_allocation
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 🧩 Database Constraints

### Fund

```text
symbol
UNIQUE
NOT NULL
```

### Fund NAV

```text
fund_id + nav_date
```

is unique.

### SIP Allocation

```text
sip_id + allocation_date
```

is unique.

## ⚠️ Exception Handling

The application uses custom runtime exceptions and centralized
exception handling.

Examples:

```java
FundNotFoundException
NAVAlreadyExistsException
```

Global exception handling:

```java
@RestControllerAdvice
```

HTTP mappings:

```text
Fund not found
→ 404 NOT FOUND

Duplicate NAV
→ 409 CONFLICT
```

## 🧪 Functional Testing

Tested scenarios include:

### Fund Testing

- Create fund
- Retrieve all funds
- Retrieve fund by symbol
- Invalid fund lookup

### NAV Testing

- Create valid NAV
- Duplicate NAV for same fund/date
- Invalid fund while creating NAV
- NAV retrieval through fund APIs

### SIP Testing

- Create SIP
- Retrieve all SIPs
- Retrieve SIPs by user

### Scheduler Testing

- Due SIP execution
- NAV-based unit allocation
- Missing NAV scenario
- Idempotent re-execution

Example:

```text
SIP Amount = ₹5,000
NAV        = ₹247.35
Units      = 20.2143
```

## 📊 Performance Testing

Apache JMeter was used for load testing.

Two primary load patterns were used.

### Test 1 — High Concurrency

```text
Threads        = 1000
Loops          = 100
Total Requests = 100,000
Ramp-up        = 10 seconds
```

### Test 2 — High Iteration

```text
Threads        = 100
Loops          = 1000
Total Requests = 100,000
Ramp-up        = 10 seconds
```

Metrics recorded:

- Average Response Time
- Median
- P90
- P95
- P99
- Minimum
- Maximum
- Standard Deviation
- Error Percentage
- Throughput
- Received Data Rate
- Sent Data Rate
- Average Response Size

## 📈 Example Performance Results

### GET `/api/v1/funds`

One successful 100,000-request run:

```text
Samples      : 100,000
Average      : 39 ms
Median       : 19 ms
P90          : 77 ms
P95          : 143 ms
P99          : 470 ms
Min          : 0 ms
Max          : 3074 ms
Error        : 0.00%
Throughput   : 4987.8 req/sec
```

These are local benchmark observations and should not be interpreted
as universal production capacity.

### GET `/api/v1/funds/{symbol}`

Test 1:

```text
Samples      : 100,000
Average      : 230 ms
Median       : 205 ms
P90          : 419 ms
P95          : 571 ms
P99          : 996 ms
Min          : 0 ms
Max          : 3022 ms
Std Dev      : 192.19 ms
Error        : 0.00%
Throughput   : 2673.2 req/sec
```

Test 2:

```text
Samples      : 100,000
Average      : 16 ms
Median       : 6 ms
P90          : 44 ms
P95          : 70 ms
P99          : 133 ms
Min          : 0 ms
Max          : 416 ms
Std Dev      : 26.86 ms
Error        : 0.00%
Throughput   : 3844.4 req/sec
```

### GET `/api/v1/sips/user`

Test 1:

```text
Samples      : 100,000
Average      : 197 ms
Median       : 141 ms
P90          : 372 ms
P95          : 499 ms
P99          : 996 ms
Min          : 0 ms
Max          : 5264 ms
Std Dev      : 197.32 ms
Error        : 0.00%
Throughput   : 3903.2 req/sec
```

Test 2:

```text
Samples      : 100,000
Average      : 5 ms
Median       : 4 ms
P90          : 11 ms
P95          : 15 ms
P99          : 33 ms
Min          : 0 ms
Max          : 201 ms
Std Dev      : 7.05 ms
Error        : 0.00%
Throughput   : 6808.3 req/sec
```

### GET `/api/v1/sips/user/{userId}`

Test 1:

```text
Samples      : 100,000
Average      : 1 ms
Median       : 1 ms
P90          : 2 ms
P95          : 3 ms
P99          : 6 ms
Min          : 0 ms
Max          : 59 ms
Std Dev      : 1.26 ms
Error        : 0.00%
Throughput   : 9951.2 req/sec
```

Test 2:

```text
Samples      : 100,000
Average      : 4 ms
Median       : 3 ms
P90          : 9 ms
P95          : 14 ms
P99          : 35 ms
Min          : 0 ms
Max          : 153 ms
Std Dev      : 6.98 ms
Error        : 0.00%
Throughput   : 8724.5 req/sec
```

## ⚠️ Scalability Observation — GET All SIPs

The endpoint:

```http
GET /api/v1/sips/user
```

returns all SIP records.

When the database contained approximately:

```text
200,004 SIP records
```

the response became very large because every request attempted to return
the complete dataset.

A smaller 50-request test produced:

```text
Requests     : 50
Average      : 3118 ms
Min          : 2104 ms
Max          : 6164 ms
Error        : 0%
Throughput   : 1.3 req/sec
Avg Response : ~30.7 MB/request
```

This demonstrates the scalability limitation of an unpaginated
collection endpoint when the dataset grows.

The issue can be represented as:

```text
200,004 DB records
        ↓
Load entire dataset
        ↓
Convert entire dataset to response objects
        ↓
Serialize large JSON response
        ↓
Transfer large response for every request
```

## 📌 Scalability Considerations

For production-scale implementation:

### Pagination

Use:

```text
page
size
```

Example:

```text
GET /api/v1/sips?page=0&size=50
```

### Filtering

Support bounded queries using:

```text
userId
fund
active status
date range
```

### Database Indexes

Frequently queried columns can be indexed:

```text
user_id
fund_id
deduction_day
active
```

### DTO Projections

Retrieve only the fields required by the API where appropriate.

### Connection Pool Tuning

Tune the database connection pool according to:

- Database capacity
- Request concurrency
- Application instances
- Query execution time

### Horizontal Scaling

Multiple application instances can be deployed behind a load balancer.

Scheduled processing would require additional coordination to prevent
multiple instances from executing the same SIP simultaneously.

## 🔍 Important Performance Testing Note

The JMeter numbers are local benchmark measurements.

They depend on:

- Local machine hardware
- JVM state
- PostgreSQL state
- Database size
- Connection pool
- Background processes
- JMeter configuration
- Network conditions
- Application state

Therefore, these numbers should not be presented as guaranteed
production capacity.

The purpose of performance testing was to identify:

```text
Latency
Throughput
Error behavior
Dataset-size impact
Potential scalability bottlenecks
```

## 🧠 Design Decisions

### Why Spring Boot?

Spring Boot provides:

- Dependency injection
- REST API development
- Scheduling
- Configuration management
- Exception handling
- JPA/Hibernate integration

### Why Spring Data JPA?

Spring Data JPA reduces boilerplate database access code while allowing
repository methods to express business queries.

Examples:

```java
findBySymbol(...)
findByFundAndNavDate(...)
findByActiveTrueAndDeductionDay(...)
findBySipAndAllocationDate(...)
```

### Why PostgreSQL?

PostgreSQL provides:

- ACID transactions
- Relational constraints
- Decimal support
- Indexing
- Referential integrity
- Production-grade relational database capabilities

### Why BigDecimal?

Financial calculations require deterministic decimal arithmetic.

Therefore:

```java
BigDecimal
```

is used instead of:

```java
double
float
```

### Why Database-Level Idempotency?

Application logic alone should not be the only protection against
duplicate execution.

The unique constraint:

```text
(sip_id, allocation_date)
```

ensures that the database itself enforces the uniqueness rule.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web | REST APIs |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| PostgreSQL 17.10 | Relational database |
| Maven | Build and dependency management |
| Apache JMeter | Load & performance testing |
| Git/GitHub | Version control |

## ▶️ Running the Project

### 1. Clone Repository

```bash
git clone <repository-url>
cd sip-allocation-engine
```

### 2. Configure PostgreSQL

```sql
CREATE DATABASE sip_allocation;
```

### 3. Configure Application Properties

```properties
spring.application.name=sip-allocation-engine

spring.datasource.url=jdbc:postgresql://localhost:5432/sip_allocation
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 4. Build

```bash
mvn clean install
```

### 5. Run

```bash
mvn spring-boot:run
```

Application:

```text
http://localhost:8080
```

## 🔬 Example End-to-End Flow

### Step 1 — Create Fund

```http
POST /api/v1/funds
```

```json
{
  "symbol": "HDFC-FLEXI",
  "name": "HDFC Flexi Cap Fund",
  "category": "EQUITY",
  "active": true
}
```

### Step 2 — Create NAV

```http
POST /api/v1/nav
```

```json
{
  "fundSymbol": "HDFC-FLEXI",
  "navDate": "2026-09-15",
  "navPrice": 247.35
}
```

### Step 3 — Create SIP

```http
POST /api/v1/sips
```

```json
{
  "userId": 101,
  "fundSymbol": "HDFC-FLEXI",
  "monthlyAmount": 5000,
  "deductionDay": 15
}
```

### Step 4 — Scheduler Finds Due SIP

```text
User       = 101
Fund       = HDFC-FLEXI
Amount     = ₹5,000
Deduction  = 15
```

### Step 5 — NAV Lookup

```text
NAV = ₹247.35
```

### Step 6 — Unit Calculation

```text
5000 / 247.35
= 20.214271...
```

Rounded:

```text
20.2143
```

### Step 7 — Allocation Record

The system creates:

```text
SIPAllocationRecord
```

containing:

```text
SIP
Allocation Date
Amount
NAV
Units Allocated
```

### Step 8 — Idempotency

```text
Allocation already exists
        ↓
Skip execution
```

No duplicate allocation is created.

## 🚧 Current Scope

Included:

- Fund management
- NAV management
- SIP creation
- SIP retrieval
- Scheduled SIP execution
- NAV-based unit allocation
- BigDecimal financial calculations
- Idempotency
- PostgreSQL persistence
- Global exception handling
- Functional testing
- JMeter performance testing

## ❌ Out of Scope

- Investor holdings
- Detailed execution audit logs
- Elaborate accounting ledger
- Manual batch execution API
- Complex transaction orchestration
- Security/JWT/OAuth2
- Kafka
- External NAV provider
- Payment gateway
- Bank debit integration
- Microservices
- Recommendation engine
- Live/intraday NAV
- External mutual fund catalog API

## 🔮 Future Improvements

Potential enhancements:

1. Pagination
2. Authentication and authorization
3. External NAV integration
4. Payment integration
5. Distributed scheduler coordination
6. Event-driven processing
7. More sophisticated transaction management
8. Monitoring and observability
9. Better API filtering and pagination

Possible observability stack:

```text
Micrometer
Prometheus
Grafana
Actuator
```

## 💡 Project Highlights

This project demonstrates practical backend concepts relevant to FinTech:

```text
Java
Spring Boot
REST APIs
Spring Data JPA
Hibernate
PostgreSQL
BigDecimal
Financial Calculations
Database Constraints
Idempotency
Scheduled Processing
Exception Handling
DTO-based APIs
Load Testing
Performance Analysis
Scalability Analysis
```

The project also demonstrates an important engineering principle:

> A system can perform well on a small dataset while still having scalability limitations when the data volume and response size increase.

The JMeter testing helped identify this behavior rather than only measuring
ideal small-dataset performance.

## 📊 Final Project Flow

```text
                    ┌───────────────┐
                    │     Fund      │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │      NAV      │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │  Investor SIP │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │   Scheduler   │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │  Due SIPs     │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │  Today's NAV  │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │ Amount / NAV  │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │ BigDecimal(4) │
                    └───────┬───────┘
                            │
                            ▼
                  ┌───────────────────┐
                  │ Idempotency Check │
                  └─────────┬─────────┘
                            │
                  ┌─────────┴─────────┐
                  │                   │
                  ▼                   ▼
             Already Done          New
                  │                   │
                  ▼                   ▼
                Skip          Allocation Record
                                      │
                                      ▼
                                  PostgreSQL
```

## 📌 Conclusion

The Mutual Fund SIP & NAV Allocation Engine provides a focused backend
implementation of a mutual-fund SIP processing workflow.

The project combines:

```text
REST APIs
+
Relational Data Modeling
+
Scheduled Processing
+
Financial Calculations
+
Idempotency
+
Database Constraints
+
Performance Testing
```

It is intentionally designed as a modular backend project that can later
evolve toward authentication, external NAV integration, payment processing,
distributed scheduling, event-driven architecture, observability, and
production-scale optimization.

---

## 👨‍💻 Author

**Anurag Nikumbh**

Java Backend Developer | Spring Boot | FinTech

GitHub:

```text
<your-github-profile>
```

---

## ⭐ Project Objective

The primary objective of this project is to demonstrate the design and
implementation of a practical Java/Spring Boot backend system with
real-world FinTech characteristics such as financial precision,
scheduled processing, database integrity, idempotency, and
performance analysis.