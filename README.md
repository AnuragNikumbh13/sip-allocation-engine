# Mutual Fund SIP & NAV Allocation Engine

A Spring Boot backend system that manages mutual funds, daily NAV records, investor SIP mandates, automated SIP execution, NAV-based unit allocation, and database-enforced idempotency.

---

## 🚀 Key Features

- Mutual fund management
- Daily NAV management
- Investor SIP management
- Automated scheduled SIP execution
- NAV-based unit allocation
- Financial calculations using `BigDecimal`
- Database-enforced idempotency
- PostgreSQL persistence
- Global exception handling
- REST APIs
- Functional testing
- Apache JMeter performance testing
- Dataset-scale scalability analysis

---

## 🏦 Business Workflow

The system models a simplified mutual-fund SIP workflow:

Fund
↓
Daily NAV
↓
Investor SIP
↓
Scheduled SIP Execution
↓
Find Due SIPs
↓
Find Today's NAV
↓
Calculate Units
↓
Idempotency Check
↓
Create SIP Allocation Record
↓
PostgreSQL

---

## 💰 Core Business Formula

For every SIP execution:

`Units Allocated = SIP Amount / NAV`

The calculation uses Java `BigDecimal` with:

- Scale = 4
- Rounding Mode = `HALF_UP`

### Example

SIP Amount = ₹5,000  
NAV = ₹247.35

Units = 5000 / 247.35  
Units = 20.214271...  
Final Units = 20.2143

---

# 🏗️ Architecture

The application follows a layered Spring Boot architecture:

Controller  
↓  
Service  
↓  
Repository  
↓  
PostgreSQL

Scheduled SIP processing:

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

---

# 📂 Project Structure

The repository is organized as:

sip-allocation-engine/
│
├── reports/
│   ├── JMeter performance reports
│   └── Performance test evidence
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── test/
│
├── README.md
├── pom.xml
├── .gitignore
├── mvnw
└── mvnw.cmd

Java package structure:

sip_allocation_engine
├── controller
├── service
├── scheduler
├── entity
├── repository
├── dto
├── exception
└── config

---

# 🗃️ Domain Model

Fund
│
├── FundNAV
│
└── InvestorSIP
│
└── SIPAllocationRecord

### Fund

Represents a mutual fund available in the system.

Contains:

- Fund ID
- Symbol
- Name
- Category
- Active status

### FundNAV

Stores the daily NAV of a fund.

Contains:

- Fund
- NAV date
- NAV price

### InvestorSIP

Represents an investor's recurring SIP mandate.

Contains:

- User ID
- Fund
- Monthly SIP amount
- Deduction day
- Active status
- Creation timestamp

### SIPAllocationRecord

Stores the result of a successful SIP allocation.

Contains:

- SIP
- Allocation date
- SIP amount
- NAV used
- Units allocated

---

# 📊 Database Design

The application uses PostgreSQL.

Database:

`sip_allocation`

The application uses JPA/Hibernate for persistence.

## Important Constraints

### Fund

The fund symbol is:

- UNIQUE
- NOT NULL

This prevents duplicate fund symbols.

### Fund NAV

The database enforces:

`UNIQUE(fund_id, nav_date)`

This ensures that a fund cannot have multiple NAV records for the same date.

### SIP Allocation

The database enforces:

`UNIQUE(sip_id, allocation_date)`

This ensures that the same SIP cannot be allocated more than once for the same allocation date.

---

# 💵 Financial Calculation

Financial calculations use Java `BigDecimal`.

The allocation calculation is:

`monthlyAmount.divide(navPrice, 4, RoundingMode.HALF_UP)`

### Why BigDecimal?

Financial calculations require deterministic decimal arithmetic.

Using floating-point types such as `double` or `float` can introduce precision issues.

Therefore the project uses:

- `BigDecimal`
- Scale = 4
- `RoundingMode.HALF_UP`

---

# 🔄 SIP Execution Flow

The scheduled SIP process performs the following steps:

1. Get current date
2. Determine deduction day
3. Find active SIPs due today
4. Check whether allocation already exists
5. Find today's NAV
6. Calculate units
7. Round to 4 decimal places
8. Create `SIPAllocationRecord`
9. Persist allocation

If today's NAV is unavailable:

NAV unavailable  
↓  
Skip SIP execution

---

# 🛡️ Idempotent SIP Processing

SIP execution must not create duplicate allocations for the same SIP and allocation date.

The implementation uses two layers of protection.

## Application-Level Check

Before creating an allocation, the repository checks:

`findBySipAndAllocationDate(sip, today)`

If a record already exists:

Already processed  
↓  
Skip

## Database-Level Protection

The database also enforces:

`UNIQUE(sip_id, allocation_date)`

Therefore:

First execution  
↓  
SIP + Date  
↓  
Allocation created

Second execution  
↓  
Same SIP + Same Date  
↓  
Already processed  
↓  
Skip execution

The database constraint provides an additional integrity guarantee beyond the application-level check.

---

# 🌐 REST API

The application exposes 7 HTTP endpoints.

## GET Endpoints

### 1. Get All Funds

`GET /api/v1/funds`

Returns all funds.

---

### 2. Get Fund by Symbol

`GET /api/v1/funds/{symbol}`

Example:

`GET /api/v1/funds/HDFC-FLEXI`

Returns fund details along with the latest stored NAV.

---

### 3. Get All SIPs

`GET /api/v1/sips/user`

Returns all SIP records regardless of user.

> Note: This is intentionally an unpaginated collection endpoint in the current project scope.

---

### 4. Get SIPs by User

`GET /api/v1/sips/user/{userId}`

Example:

`GET /api/v1/sips/user/101`

Returns SIPs belonging to the specified user.

---

# POST Endpoints

## 5. Create Fund

`POST /api/v1/funds`

Example request:

{
"symbol": "HDFC-FLEXI",
"name": "HDFC Flexi Cap Fund",
"category": "EQUITY",
"active": true
}

---

## 6. Create NAV

`POST /api/v1/nav`

Example request:

{
"fundSymbol": "HDFC-FLEXI",
"navDate": "2026-09-15",
"navPrice": 247.35
}

---

## 7. Create SIP

`POST /api/v1/sips`

Example request:

{
"userId": 101,
"fundSymbol": "HDFC-FLEXI",
"monthlyAmount": 5000,
"deductionDay": 15
}

---

# ⏰ Automated SIP Processing

The application uses Spring Scheduling.

The intended daily schedule is:

`@Scheduled(cron = "0 0 9 * * *")`

This executes the SIP processing job every day at 9:00 AM according to the application's runtime timezone.

The scheduler:

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

---

# ⚠️ Exception Handling

The application uses custom runtime exceptions and centralized exception handling through:

`@RestControllerAdvice`

Current custom exceptions include:

- `FundNotFoundException`
- `NAVAlreadyExistsException`

HTTP mappings:

Fund not found → 404 NOT FOUND

Duplicate NAV → 409 CONFLICT

---

# 🧪 Functional Testing

The application was tested using functional API and database scenarios.

## Fund Testing

- Create fund
- Retrieve all funds
- Retrieve fund by symbol
- Invalid fund lookup

## NAV Testing

- Create valid NAV
- Duplicate NAV for same fund/date
- Invalid fund while creating NAV
- NAV retrieval through fund APIs

## SIP Testing

- Create SIP
- Retrieve all SIPs
- Retrieve SIPs by user

## Scheduler Testing

- Due SIP execution
- NAV-based unit allocation
- Missing NAV scenario
- Idempotent re-execution
- SIP allocation record creation

Example:

SIP Amount = ₹5,000  
NAV = ₹247.35  
Units = 20.2143

---

# 📊 Performance Testing

Apache JMeter was used to load-test the REST APIs.

Two primary test configurations were used.

## Test 1 — High Concurrency

- Threads = 1000
- Loops = 100
- Total Requests = 100,000
- Ramp-up = 10 seconds

## Test 2 — High Iteration

- Threads = 100
- Loops = 1000
- Total Requests = 100,000
- Ramp-up = 10 seconds

The tests were performed locally against the Spring Boot application and PostgreSQL database.

---

# 📈 Performance Metrics

The following metrics were recorded:

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

Detailed JMeter reports are available in:

`/reports`

---

# 📌 Selected Benchmark Results

## GET `/api/v1/funds`

One successful 100,000-request run:

- Samples: 100,000
- Average: 39 ms
- Median: 19 ms
- P90: 77 ms
- P95: 143 ms
- P99: 470 ms
- Minimum: 0 ms
- Maximum: 3074 ms
- Error: 0.00%
- Throughput: 4987.8 req/sec

These are local benchmark observations and should not be interpreted as guaranteed production capacity.

---

# GET `/api/v1/funds/{symbol}`

## Test 1

- Samples: 100,000
- Average: 230 ms
- Median: 205 ms
- P90: 419 ms
- P95: 571 ms
- P99: 996 ms
- Minimum: 0 ms
- Maximum: 3022 ms
- Std Dev: 192.19 ms
- Error: 0.00%
- Throughput: 2673.2 req/sec

## Test 2

- Samples: 100,000
- Average: 16 ms
- Median: 6 ms
- P90: 44 ms
- P95: 70 ms
- P99: 133 ms
- Minimum: 0 ms
- Maximum: 416 ms
- Std Dev: 26.86 ms
- Error: 0.00%
- Throughput: 3844.4 req/sec

---

# GET `/api/v1/sips/user`

## Test 1

- Samples: 100,000
- Average: 197 ms
- Median: 141 ms
- P90: 372 ms
- P95: 499 ms
- P99: 996 ms
- Minimum: 0 ms
- Maximum: 5264 ms
- Std Dev: 197.32 ms
- Error: 0.00%
- Throughput: 3903.2 req/sec

## Test 2

- Samples: 100,000
- Average: 5 ms
- Median: 4 ms
- P90: 11 ms
- P95: 15 ms
- P99: 33 ms
- Minimum: 0 ms
- Maximum: 201 ms
- Std Dev: 7.05 ms
- Error: 0.00%
- Throughput: 6808.3 req/sec

---

# GET `/api/v1/sips/user/{userId}`

## Test 1

- Samples: 100,000
- Average: 1 ms
- Median: 1 ms
- P90: 2 ms
- P95: 3 ms
- P99: 6 ms
- Minimum: 0 ms
- Maximum: 59 ms
- Std Dev: 1.26 ms
- Error: 0.00%
- Throughput: 9951.2 req/sec

## Test 2

- Samples: 100,000
- Average: 4 ms
- Median: 3 ms
- P90: 9 ms
- P95: 14 ms
- P99: 35 ms
- Minimum: 0 ms
- Maximum: 153 ms
- Std Dev: 6.98 ms
- Error: 0.00%
- Throughput: 8724.5 req/sec

---

# ⚠️ Scalability Observation — GET All SIPs

The endpoint:

`GET /api/v1/sips/user`

returns all SIP records regardless of user.

This design behaves differently as the underlying dataset grows.

When the database contained approximately:

`200,004 SIP records`

the endpoint had to retrieve and serialize the complete dataset for each request.

A smaller 50-request test against this dataset produced:

- Requests: 50
- Average: 3118 ms
- Minimum: 2104 ms
- Maximum: 6164 ms
- Error: 0%
- Throughput: 1.3 req/sec
- Average response size: approximately 30.7 MB/request

This demonstrates a scalability limitation of an unpaginated collection endpoint when the dataset becomes large.

The processing path can be represented as:

200,004 DB Records  
↓  
Retrieve complete dataset  
↓  
Map records to response objects  
↓  
Serialize large JSON response  
↓  
Transfer large response  
↓  
Repeat for every request

The observation highlights why bounded result sets and pagination are important for production-scale collection APIs.

---

# 🧠 Engineering Observation

The performance tests demonstrated that API performance depends not only on request concurrency but also on:

- Dataset Size
- Query Pattern
- Object Mapping
- JSON Serialization
- Response Size

An endpoint may perform well with a small dataset while degrading significantly when the same endpoint returns a much larger dataset.

This was observed with the unpaginated SIP collection endpoint.

---

# 📈 Scalability Considerations

For a production-scale implementation, the following improvements would be appropriate.

## 1. Pagination

Instead of returning the entire collection, use bounded responses such as:

`GET /api/v1/sips?page=0&size=50`

or user-specific pagination.

---

## 2. Filtering

Support bounded queries using filters such as:

- userId
- fund
- active status
- date range

---

## 3. Database Indexes

Frequently queried columns can be indexed, for example:

- user_id
- fund_id
- deduction_day
- active

---

## 4. DTO Projections

Where appropriate, retrieve only the fields required by the API instead of loading unnecessary entity data.

---

## 5. Connection Pool Tuning

Database connection pool configuration should be tuned according to:

- Database capacity
- Request concurrency
- Query execution time
- Application instances

---

## 6. Horizontal Scaling

Multiple application instances can be deployed behind a load balancer.

Scheduled SIP processing would require coordination in a multi-instance deployment so that the same SIP is not processed concurrently by multiple instances.

---

# 🔍 Performance Testing Notes

The JMeter results are local benchmark measurements.

Results depend on:

- Local machine hardware
- JVM state
- PostgreSQL state
- Database size
- Connection pool configuration
- Background processes
- JMeter configuration
- Application state

Therefore, the benchmark results should not be interpreted as guaranteed production capacity.

The purpose of the performance testing was to analyze:

- Latency
- Throughput
- Error Behavior
- Response Size
- Concurrency Behavior
- Dataset-Size Impact
- Scalability Limitations

---

# 🧠 Design Decisions

## Why Spring Boot?

Spring Boot provides:

- Dependency injection
- REST API development
- Scheduling
- Configuration management
- Exception handling
- JPA/Hibernate integration

---

## Why Spring Data JPA?

Spring Data JPA reduces database-access boilerplate while allowing repository methods to express business queries.

Examples:

- `findBySymbol(...)`
- `findByFundAndNavDate(...)`
- `findByActiveTrueAndDeductionDay(...)`
- `findBySipAndAllocationDate(...)`

---

## Why PostgreSQL?

PostgreSQL provides:

- ACID transactions
- Relational constraints
- Decimal support
- Indexing
- Referential integrity
- Production-grade relational database capabilities

---

## Why BigDecimal?

Financial calculations require deterministic decimal arithmetic.

Therefore the project uses:

`BigDecimal`

instead of:

`double`  
`float`

---

## Why Database-Level Idempotency?

Application-level checks are useful, but the database should also enforce important business invariants.

The unique constraint:

`(sip_id, allocation_date)`

ensures that duplicate allocation records cannot be stored for the same SIP and date.

---

# 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| PostgreSQL 17.10 | Relational database |
| Maven | Build and dependency management |
| Apache JMeter | Load and performance testing |
| Git | Version control |
| GitHub | Source code hosting |

---

# ▶️ Running the Project

## 1. Clone the Repository

`git clone https://github.com/AnuragNikumbh13/sip-allocation-engine.git`

`cd sip-allocation-engine`

## 2. Create PostgreSQL Database

`CREATE DATABASE sip_allocation;`

## 3. Configure Local Database Credentials

The project keeps local configuration outside version control.

Example configuration:

`spring.application.name=sip-allocation-engine`

`spring.datasource.url=jdbc:postgresql://localhost:5432/sip_allocation`

`spring.datasource.username=postgres`

`spring.datasource.password=${DB_PASSWORD}`

`spring.jpa.hibernate.ddl-auto=update`

`spring.jpa.show-sql=true`

`spring.jpa.properties.hibernate.format_sql=true`

Set the database password through the local environment.

The local `application.properties` file is intentionally excluded from Git using `.gitignore`.

---

## 4. Build

`mvn clean install`

## 5. Run

`mvn spring-boot:run`

Application:

`http://localhost:8080`

---

# 🔬 Example End-to-End Flow

## Step 1 — Create Fund

`POST /api/v1/funds`

Example:

{
"symbol": "HDFC-FLEXI",
"name": "HDFC Flexi Cap Fund",
"category": "EQUITY",
"active": true
}

---

## Step 2 — Create NAV

`POST /api/v1/nav`

Example:

{
"fundSymbol": "HDFC-FLEXI",
"navDate": "2026-09-15",
"navPrice": 247.35
}

---

## Step 3 — Create SIP

`POST /api/v1/sips`

Example:

{
"userId": 101,
"fundSymbol": "HDFC-FLEXI",
"monthlyAmount": 5000,
"deductionDay": 15
}

---

## Step 4 — Scheduler Finds Due SIP

User = 101  
Fund = HDFC-FLEXI  
Amount = ₹5,000  
Deduction Day = 15

---

## Step 5 — NAV Lookup

NAV = ₹247.35

---

## Step 6 — Unit Calculation

5000 / 247.35  
= 20.214271...

Rounded to:

20.2143

---

## Step 7 — Allocation Record

The system creates:

`SIPAllocationRecord`

containing:

- SIP
- Allocation Date
- Amount
- NAV
- Units Allocated

---

## Step 8 — Idempotency

If the scheduler attempts the same SIP again for the same date:

Allocation already exists  
↓  
Skip execution

No duplicate allocation is created.

---

# 📋 Performance Reports

Detailed JMeter reports and test evidence are maintained in:

`/reports`

The reports contain:

- HTTP Request Configuration
- Summary Report
- Aggregate Report
- Performance Results
- Test Comparison
- Analysis
- Final Status

---

# 🚧 Current Scope

Included:

- Fund management
- NAV management
- SIP creation
- SIP retrieval
- Scheduled SIP execution
- NAV-based unit allocation
- BigDecimal financial calculations
- Database-level idempotency
- PostgreSQL persistence
- Global exception handling
- Functional testing
- JMeter performance testing
- Dataset-scale scalability analysis

---

# ❌ Out of Scope

The following are intentionally outside the current project scope:

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

---

# 🔮 Future Improvements

Potential enhancements include:

1. Pagination and bounded collection APIs
2. Authentication and authorization
3. External NAV integration
4. Payment integration
5. Distributed scheduler coordination
6. Event-driven processing
7. Improved transaction management
8. Monitoring and observability
9. API filtering and pagination
10. Production-grade deployment configuration

Possible observability stack:

Spring Boot Actuator  
↓  
Micrometer  
↓  
Prometheus  
↓  
Grafana

---

# 💡 Project Highlights

This project demonstrates practical backend engineering concepts relevant to FinTech systems:

- Java
- Spring Boot
- REST APIs
- Spring Data JPA
- Hibernate
- PostgreSQL
- BigDecimal
- Financial Calculations
- Database Constraints
- Idempotency
- Scheduled Processing
- Exception Handling
- DTO-based APIs
- Load Testing
- Performance Analysis
- Scalability Analysis

A key engineering observation from the project was:

> A system can perform well on a small dataset while still having scalability limitations when data volume and response size increase.

The JMeter testing helped identify this behavior instead of evaluating the system only under small-dataset conditions.

---

# 📊 Final System Flow

Fund
↓
NAV
↓
Investor SIP
↓
Scheduler
↓
Due SIPs
↓
Today's NAV
↓
Amount / NAV
↓
BigDecimal Calculation
↓
Idempotency Check
↓
Allocation Record
↓
PostgreSQL

---

# 👨‍💻 Author

**Anurag Nikumbh**

Java Backend Developer | Spring Boot | FinTech

GitHub:

https://github.com/AnuragNikumbh13

---

# ⭐ Project Objective

The primary objective of this project is to demonstrate the design and implementation of a practical Java/Spring Boot backend system with real-world FinTech characteristics such as:

- Financial Precision
- Scheduled Processing
- Database Integrity
- Idempotency
- REST API Design
- Relational Data Modeling
- Performance Testing
- Scalability Analysis

The project is intentionally focused on a manageable backend scope while providing a foundation that could later evolve toward authentication, external NAV integration, payment processing, distributed scheduling, event-driven architecture, observability, and production-scale optimization.