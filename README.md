# Spring Boot Enterprise Template

### Build features, not infrastructure.

A production-ready Spring Boot foundation that eliminates repetitive setup, reduces AI prompt engineering, and helps teams start building scalable backend services from day one.

<br>

---

## Who Is This For?

This template is designed for anyone who wants to turn an idea into a modern, scalable backend without spending the first stage of the project rebuilding technical infrastructure.

It is useful for:

* **Java/Spring Boot developers** starting new projects
* **Backend engineers** building REST APIs
* **Development teams** establishing a consistent enterprise backend foundation
* **Freelancers and consultants** repeatedly creating Spring Boot applications
* **Developers learning** production-oriented Spring Boot practices
* **AI-assisted developers** using Claude Code, Codex, Cursor, Windsurf, Gemini CLI, ChatGPT, or similar tools
* **Founders and business owners** experimenting with their own product ideas using AI coding assistants instead of immediately hiring a software development team
* **Small businesses and startups** validating an MVP or proof of concept before investing heavily in development
* **Teams that want** testing, CI/CD, security, database migrations, and deployment foundations from the beginning

### For Founders and Business Owners

You don't necessarily need to be a professional software engineer to start experimenting with a software idea today.

With an AI coding assistant, you can describe your requirements in natural language, iterate on the application, and progressively turn an idea into a working product.

This template provides the technical foundation that AI coding assistants would otherwise need to build from scratch:

* Authentication and authorization
* PostgreSQL database
* Database migrations
* REST API structure
* Environment profiles
* Testing infrastructure
* Docker
* CI/CD
* API documentation
* Health monitoring
* Enterprise-oriented project organization

Instead of spending your early AI sessions asking:

> *"How do I set up the backend?"*

you can start asking:

> *"How should my application implement this business idea?"*

The template doesn't replace software engineering expertise, security review, testing, or production operations. **It simply gives you a much stronger starting point for experimenting, validating ideas, and building with AI.**

Whether you're an experienced backend engineer or a founder exploring your first software product, the goal is the same:

> **Start with a solid engineering foundation and spend your time building the product.**

---

## What This Template Is Not

This is not intended to be:

* A complete business application
* A replacement for Spring Initializr
* A framework that dictates every architectural decision
* A collection of every possible Spring ecosystem component
* A guarantee that an application is automatically production-ready

It provides a strong foundation. Individual applications still need to make appropriate decisions around security, infrastructure, observability, deployment, compliance, scalability, and business requirements.

---

## Why This Template?

Starting a new enterprise backend often means spending hours—or days—repeating the same setup:

* Configure Spring Security and JWT authentication
* Set up PostgreSQL
* Configure database migrations
* Configure Docker
* Set up environment-specific profiles
* Configure OpenAPI / Swagger
* Add health monitoring
* Configure unit and integration testing
* Set up Testcontainers
* Configure code coverage
* Build a CI/CD pipeline
* Establish a scalable package structure

None of these tasks are the business feature you're actually trying to build.

This template provides that foundation upfront.

**Clone it, configure it, and start building your domain instead of rebuilding your infrastructure.**

---

## Built for Modern Development and AI-Assisted Coding

This project is particularly useful when working with AI coding assistants such as Claude Code, Codex, Cursor, Windsurf, Gemini CLI, or ChatGPT.

Instead of repeatedly prompting an AI assistant to:

> "Configure Spring Security."

> "Add PostgreSQL."

> "Set up Flyway."

> "Add Testcontainers."

> "Configure GitHub Actions."

> "Add JaCoCo."

> "Create Docker configuration."

> "Add Swagger."

> "Create test profiles."

the infrastructure is already established.

Your AI assistant can start with the actual application requirements:

> **"Add an Order domain with CRUD operations, validation, authorization, migrations, unit tests, and integration tests."**

This reduces repetitive prompt engineering, avoids unnecessary setup iterations, and allows AI-assisted development to focus on **business logic rather than project plumbing**.

---

## What You Get

### Application Foundation

* Modern Spring Boot architecture
* Java 25
* Domain-oriented package organization
* Scalable separation of application concerns
* Environment-specific configuration

### Security

* Spring Security
* JWT-based authentication
* JJWT
* Password hashing
* Authentication and authorization foundation
* Protected API endpoints

### Database

* PostgreSQL
* Flyway database migrations
* Version-controlled schema changes
* Separate test database configuration

### API Development

* REST API foundation
* Bean Validation
* Global exception handling
* OpenAPI documentation
* Swagger UI

### Operations

* Spring Boot Actuator
* Application health checks
* Liveness/readiness support
* Docker-ready configuration

### Testing

* JUnit Jupiter
* Mockito
* Unit tests
* Integration tests
* Testcontainers
* PostgreSQL integration testing
* JaCoCo code coverage

### DevOps

* Docker
* Docker Compose
* GitHub Actions
* Automated build and test pipeline
* CI-ready project structure

---

## Technology Stack

| Technology               | Purpose                              |
|--------------------------|--------------------------------------|
| **Java 25**              | Application runtime and language     |
| **Spring Boot 4.1**      | Application framework                |
| **Spring Security**      | Authentication and authorization     |
| **JJWT**                 | JWT creation and validation          |
| **PostgreSQL 18**        | Relational database                  |
| **Flyway**               | Database schema versioning           |
| **SpringDoc OpenAPI**    | OpenAPI specification and Swagger UI |
| **Spring Boot Actuator** | Health and application monitoring    |
| **Docker**               | Containerization                     |
| **Docker Compose**       | Local infrastructure                 |
| **JUnit Jupiter**        | Unit and integration testing         |
| **Mockito**              | Mock-based unit testing              |
| **Testcontainers**       | Real database integration testing    |
| **JaCoCo**               | Code coverage                        |
| **GitHub Actions**       | Continuous Integration               |

---

## Architecture

The template follows a **domain-oriented package structure** designed to remain manageable as the application grows.

Instead of organizing the entire application around technical layers such as:

```text
controller/
service/
repository/
entity/
```

the application can be organized around business domains:

```text
auth/
user/
order/
product/
...
```

Each domain can contain its own controllers, services, DTOs, repositories, entities, and related components.

This makes it easier to:

* Add new business domains
* Understand ownership of functionality
* Reduce cross-domain coupling
* Scale the application as requirements grow
* Eventually extract domains into independent services when necessary

---

## Project Structure

A typical structure looks like:

```text
src/
├── main/
│   ├── java/
│   │   └── com.example/
│   │       ├── auth/
│   │       │   ├── config/
│   │       │   ├── controller/
│   │       │   ├── dto/
│   │       │   ├── principal/
│   │       │   └── service/
│   │       │
│   │       ├── user/
│   │       │   ├── controller/
│   │       │   ├── dto/
│   │       │   ├── entity/
│   │       │   ├── repository/
│   │       │   └── service/
│   │       │
│   │       ├── config/
│   │       ├── exception/
│   │       └── ...
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       ├── application-prod.yml
│       └── db/
│           └── migration/
│
└── test/
    └── java/
```

The exact package structure can be adapted to the application's domain requirements.

---

## Getting Started

### Prerequisites

Make sure you have:

* Java 25
* Maven
* Docker
* Docker Compose
* Git

### Clone the Repository

```bash
git clone <repository-url>

cd spring-boot-enterprise-template
```

### Configure Environment Variables

Create your local environment configuration based on the provided example:

```bash
cp .env.example .env
```

Update the values according to your local environment.

**Never commit secrets or production credentials to Git.**

### Start PostgreSQL

Start the required infrastructure using Docker Compose:

```bash
docker compose up -d
```

### Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

Or build and run the application:

```bash
./mvnw clean package
java -jar target/*.jar
```

---

## Configuration Profiles

The project supports separate configuration profiles for different environments.

```text
application.yml
application-dev.yml
application-test.yml
application-prod.yml
```

### Development

Used for local development:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Test

Used by automated tests:

```bash
./mvnw test
```

### Production

Production configuration should be supplied through environment variables or an external configuration mechanism rather than committing secrets to source control.

Example:

```bash
SPRING_PROFILES_ACTIVE=prod
```

### Why Profiles?

Separating configuration by environment helps prevent:

* Development settings leaking into production
* Test configuration affecting local development
* Credentials being hardcoded
* Environment-specific behavior becoming difficult to manage

---

## Database & Flyway

Database schema changes are managed through Flyway.

Migration files are stored under:

```text
src/main/resources/db/migration/
```

Example:

```text
V1__create_users_table.sql
V2__add_user_status.sql
V3__create_refresh_tokens_table.sql
```

### Migration Rules

Use Flyway's versioned migration convention:

```text
V<version>__<description>.sql
```

For example:

```text
V4__create_orders_table.sql
```

Once a migration has been applied, **do not modify it**.

Create a new migration for subsequent changes:

```text
V5__add_order_status.sql
```

This ensures database changes remain reproducible across development, testing, CI, and production environments.

---

## Authentication & Security

The template provides a foundation for secure REST APIs using:

* Spring Security
* JWT
* JJWT
* Password hashing
* Authentication principals
* Authorization rules

Authentication-related functionality is isolated within its domain so that security concerns remain separated from business domains.

A typical request flow is:

```text
Client
   │
   ▼
Authentication Endpoint
   │
   ▼
Spring Security
   │
   ▼
JWT
   │
   ▼
Protected API
   │
   ▼
Authenticated Principal
   │
   ▼
Domain Service
```

The security configuration should be extended according to the application's authorization requirements.

---

## API Documentation

OpenAPI documentation is provided through SpringDoc.

After starting the application, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI specification can be accessed at:

```text
http://localhost:8080/v3/api-docs
```

Swagger UI allows developers to:

* Explore available endpoints
* View request and response schemas
* Test APIs interactively
* Understand authentication requirements

---

## Health & Monitoring

Spring Boot Actuator provides application health and operational endpoints.

The health endpoint is available at:

```text
http://localhost:8080/actuator/health
```

The template is designed to support health monitoring and readiness/liveness checks suitable for containerized environments.

This provides a foundation for integration with:

* Docker
* Kubernetes
* Container orchestration
* Cloud deployment platforms
* Monitoring systems

Only the actuator endpoints appropriate for the deployment environment should be exposed publicly.

---

## Testing

Testing is treated as part of the application foundation rather than something added later.

The project supports:

### Unit Tests

Use unit tests to verify individual components in isolation.

Typical examples include:

* Services
* Validators
* Security components
* Utility classes

Run unit tests with:

```bash
./mvnw test
```

Example structure:

```text
src/test/java/
└── com.example/
    └── user/
        └── service/
            └── UserServiceTest.java
```

---

### Integration Tests

Integration tests verify that multiple application components work together.

Examples include:

* Controller → Service → Repository
* Spring Security integration
* Database persistence
* Flyway migrations
* REST API behavior

Integration tests should verify real application behavior rather than simply duplicating unit tests.

---

## Testcontainers

Testcontainers allows integration tests to run against a real PostgreSQL container rather than relying on an in-memory database.

Typical flow:

```text
JUnit Test
    │
    ▼
Testcontainers
    │
    ▼
PostgreSQL Container
    │
    ▼
Spring Boot Application
    │
    ▼
Repository / Service / Controller
```

This provides greater confidence that the application behaves correctly against the same database technology used in real environments.

Docker must be available when running Testcontainers-based integration tests.

---

## Code Coverage

JaCoCo is configured to measure test coverage.

Run:

```bash
./mvnw clean verify
```

After the build completes, the HTML report is typically available under:

```text
target/site/jacoco/index.html
```

Coverage should be treated as a quality indicator rather than the sole measure of test quality.

The goal is meaningful tests that verify application behavior.

---

## Docker

The project is designed to run in containerized environments.

Docker can be used for local infrastructure and application deployment.

Start the required services:

```bash
docker compose up -d
```

Stop them with:

```bash
docker compose down
```

To rebuild:

```bash
docker compose up --build
```

Container configuration should remain environment-driven so that the same application image can be promoted through different environments without rebuilding it for each environment.

---

## CI/CD

GitHub Actions provides an automated CI foundation.

The CI pipeline is designed to verify that changes:

1. Compile successfully
2. Pass automated tests
3. Generate the required build artifacts
4. Maintain expected code quality

A typical workflow is:

```text
Git Push / Pull Request
        │
        ▼
GitHub Actions
        │
        ├── Build
        ├── Unit Tests
        ├── Integration Tests
        ├── Testcontainers
        ├── JaCoCo
        │
        ▼
    Build Result
```

This means every change can be validated automatically before being merged.

---

## Adding a New Domain

One of the primary goals of this template is making it easy to add new business capabilities.

For example, to add an `Order` domain:

```text
order/
├── controller/
├── dto/
├── entity/
├── repository/
└── service/
```

A typical implementation flow is:

```text
1. Define domain requirements
2. Create database migration
3. Create entity
4. Create repository
5. Create DTOs
6. Implement service
7. Create controller
8. Add validation
9. Add authorization rules
10. Add unit tests
11. Add integration tests
12. Update API documentation
```

This structure also works particularly well with AI-assisted development because the boundaries of a new feature are clear.

---

## AI-Assisted Development

This template is intentionally designed to provide strong context to AI coding assistants.

Instead of starting every project with prompts such as:

```text
Set up Spring Security.

Add JWT authentication.

Configure PostgreSQL.

Configure Flyway.

Add Docker.

Configure Testcontainers.

Add Swagger.

Configure Actuator.

Add GitHub Actions.

Add JaCoCo.

Create test profiles.

Create production profiles.
```

those concerns are already established.

This allows prompts to focus on actual application requirements:

```text
Create an Order domain.

Requirements:
- authenticated users can create orders
- users can view their own orders
- administrators can view all orders
- add PostgreSQL persistence
- create Flyway migration
- add validation
- add unit tests
- add integration tests
```

The template therefore acts as **pre-built context for AI-assisted software development**.

The AI spends less effort generating infrastructure and more effort implementing the application's actual requirements.

---

## Companion Frontend

Looking for a frontend to pair with this backend?

The [Next.js Enterprise Template](https://github.com/ar-ubaid/nextjs-enterprise-template) provides a modern React/Next.js foundation designed to integrate seamlessly with this Spring Boot backend.

It includes:

* **TypeScript** for type safety
* **Tailwind CSS** for responsive design
* **Authentication integration** with JWT token management
* **REST API client** pre-configured for your backend
* **Environment-based configuration** for development and production
* **Production-ready** build and deployment setup

Just as this template eliminates backend infrastructure decisions, the frontend template handles the modern React ecosystem setup so you can focus on building your user interface.

**Backend + Frontend = Complete Application Foundation**

Start building full-stack applications without spending weeks on project configuration.

Repository: [nextjs-enterprise-template](https://github.com/ar-ubaid/nextjs-enterprise-template)

---

## Development Philosophy

This project follows a few core principles:

### Business logic first

Infrastructure should enable business development, not become the project itself.

### Secure by default

Security should be established before business endpoints are exposed.

### Test from the beginning

Testing infrastructure should exist before the application becomes large.

### Database changes are version-controlled

Every schema change should be reproducible.

### Environment configuration is externalized

Secrets and environment-specific configuration should not be hardcoded.

### Domains should remain understandable

Project organization should make it obvious where a new business capability belongs.

### Automation should start early

Builds and tests should run automatically through CI.

---

## Roadmap

Potential future improvements include:

* [ ] Refresh token implementation
* [ ] Email verification
* [ ] Password reset workflow
* [ ] Role and permission management
* [ ] OAuth2 / OpenID Connect examples
* [ ] Redis integration example
* [ ] API rate limiting
* [ ] Structured JSON logging
* [ ] Micrometer metrics
* [ ] Distributed tracing
* [ ] Virtual Threads examples
* [ ] Kubernetes deployment examples
* [ ] Production observability stack
* [ ] Optional Spring Modulith architecture

The roadmap is intentionally incremental. The goal is to keep the core template focused and useful rather than turning it into an everything-included framework.

---

## Contributing

Contributions, suggestions, and improvements are welcome.

If you find a bug or have an idea for improving the template:

1. Open an issue describing the problem or proposal.
2. Explain the reasoning behind the change.
3. Keep changes focused and consistent with the project's goals.
4. Include or update tests where appropriate.

---

## License

This project is licensed under the MIT License.

See the `LICENSE` file for details.

Third-party dependencies are distributed under their respective licenses.

---

## Final Thought

Starting a new backend application should not mean repeatedly solving the same infrastructure problems.

**Clone the foundation. Configure your environment. Build your domain.**

### Build features, not infrastructure.
