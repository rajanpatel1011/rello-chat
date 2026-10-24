# Spring Boot 4.0 Upgrade Specification — Rello Chat

| Field           | Value                                      |
|-----------------|--------------------------------------------|
| **Document**    | Upgrade Specification                      |
| **Application** | Rello Chat                                 |
| **Author**      | Engineering                                |
| **Created**     | 2026-10-24                                 |
| **Status**      | Draft                                      |
| **From**        | Spring Boot 3.3.10 / Spring Framework 6.x  |
| **To**          | Spring Boot 4.0.x / Spring Framework 7.x   |
| **Java**        | 21 (no change)                             |

---

## 1  Objective

Upgrade the Rello Chat application from Spring Boot 3.3.10 to Spring Boot 4.0.x
while maintaining full feature parity, security posture, and production stability.
The upgrade brings the project onto the latest LTS-track release line and adopts
Spring Framework 7, Spring Security 7, Jakarta EE 11, and Jackson 3.

---

## 2  Scope

### 2.1  In Scope

| Area | Details |
|------|---------|
| Spring Boot parent BOM | `3.3.10` → `4.0.x` |
| Spring Security | 6.x → 7.x |
| Jackson | 2.x → 3.x (transitive, auto-configured) |
| Null-safety annotations | `org.springframework.lang` → JSpecify |
| Configuration properties | Rename deprecated keys |
| Build plugins | `maven-compiler-plugin`, `dependency-check-maven` |
| Third-party dependencies | Testcontainers, Azure Identity SDK |
| CI/CD pipeline | Azure Pipelines Maven version |
| Test suite | Compatibility verification |

### 2.2  Out of Scope

| Area | Rationale |
|------|-----------|
| Java version upgrade (21 → 25) | Can be done independently later |
| External message broker (Redis/RabbitMQ) | Separate scalability initiative |
| Database migration (SQL Server → other) | Not related to framework upgrade |
| New feature development | Deferred until upgrade is validated |
| Dockerfile base image change | `eclipse-temurin:21-jre-jammy` remains valid |

---

## 3  Current Architecture Inventory

### 3.1  Source Files

| Package | File | Purpose |
|---------|------|---------|
| `com.rajan` | `WebsocketDemoApplication.java` | Entry point |
| `com.rajan.config` | `SecurityConfig.java` | Spring Security filter chain (lambda DSL) |
| `com.rajan.config` | `WebSocketConfig.java` | STOMP over SockJS broker config |
| `com.rajan.config` | `DataInitializer.java` | Seed data via `CommandLineRunner` |
| `com.rajan.controller` | `ChatController.java` | `@MessageMapping` STOMP handlers |
| `com.rajan.controller` | `WebSocketEventListener.java` | Connect/disconnect event handling |
| `com.rajan.controller` | `LoginController.java` | Thymeleaf login & index routes |
| `com.rajan.controller` | `UserManagementController.java` | Admin CRUD (`@PreAuthorize`) |
| `com.rajan.controller` | `HealthController.java` | `/health` REST endpoint |
| `com.rajan.error` | `ErrorHandler.java` | `StompSubProtocolErrorHandler` |
| `com.rajan.model` | `User.java` | JPA entity (Jakarta Persistence) |
| `com.rajan.model` | `ChatMessage.java` | DTO with Jakarta Validation |
| `com.rajan.repository` | `UserRepository.java` | Spring Data JPA interface |
| `com.rajan.service` | `CustomUserDetailsService.java` | `UserDetailsService` impl |

### 3.2  Configuration Files

| File | Key Contents |
|------|-------------|
| `application.properties` | JPA settings, Flyway location, Actuator exposure, Prometheus metrics |
| `application-local.properties` | H2 in-memory DB, Flyway disabled, DDL auto-create |
| `application-prod.properties` | SQL Server connection, Flyway enabled |
| `spring-logback.xml` | Console appender, Logback classic |

### 3.3  Infrastructure

| File | Purpose |
|------|---------|
| `Dockerfile` | `eclipse-temurin:21-jre-jammy`, copies `rello.jar` |
| `docker-compose.yml` | Application container |
| `docker-compose.monitoring.yml` | Prometheus monitoring stack |
| `prometheus.yml` | Scrape configuration |
| `azure-pipelines.yml` | CI — Maven 3.10.1, JDK 21 |

### 3.4  Database Migrations

| File | Description |
|------|-------------|
| `V1__create_user_tables.sql` | Creates `users` and `user_roles` tables (SQL Server dialect) |

---

## 4  Compatibility Assessment

### 4.1  Already Compatible (No Changes Required)

| Item | Evidence |
|------|----------|
| Jakarta namespace | All imports use `jakarta.persistence.*` and `jakarta.validation.*` — no `javax.*` found |
| Security pattern | Uses `SecurityFilterChain` bean + lambda DSL, not deprecated `WebSecurityConfigurerAdapter` |
| `@MockBean` / `@SpyBean` | Not used in test suite — no migration needed |
| Java 21 | Fully supported by SB4 |
| JPA entity model | Standard Jakarta Persistence annotations |

### 4.2  Requires Changes

| ID | Item | Severity | File(s) |
|----|------|----------|---------|
| C-01 | `org.springframework.lang.NonNull` → JSpecify `@NonNull` | **Compilation** | `WebSocketConfig.java` |
| C-02 | Verify starter artifact renames in POM | **Compilation** | `pom.xml` |
| C-03 | `management.metrics.export.prometheus.enabled` property | **Startup** | `application.properties` |
| C-04 | `management.metrics.tags.application` property key | **Verification** | `application.properties` |
| C-05 | `maven-compiler-plugin` 3.11.0 → 3.13.0+ | **Build** | `pom.xml` |
| C-06 | `dependency-check-maven` 8.4.0 → 11.x+ | **Build** | `pom.xml` |
| C-07 | `testcontainers.version` 1.19.7 → 1.20.x+ | **Build** | `pom.xml` |
| C-08 | `azure-identity` 1.13.0 → latest | **Build** | `pom.xml` |
| C-09 | Azure Pipelines Maven 3.10.1 → 3.9.x+ | **CI** | `azure-pipelines.yml` |
| C-10 | Spring Security 7 CSRF/session defaults | **Runtime** | `SecurityConfig.java` |
| C-11 | WebSocket/SockJS API stability | **Runtime** | `WebSocketConfig.java`, `ErrorHandler.java` |
| C-12 | Actuator endpoint & metric name changes | **Silent** | Prometheus dashboards |
| C-13 | Hibernate version bump — dialect/DDL defaults | **Silent** | `application-local.properties` |

---

## 5  Migration Phases

### Phase 0 — Stepping Stone (Spring Boot 3.5.x)

**Goal:** Surface all deprecation warnings before the breaking-change cliff.

| Step | Action | Acceptance Criteria |
|------|--------|---------------------|
| 0.1 | Bump `spring-boot-starter-parent` to latest `3.5.x` | Compiles cleanly |
| 0.2 | Fix deprecated property `management.metrics.export.prometheus.enabled` → `management.prometheus.metrics.export.enabled` | No deprecation warnings at startup |
| 0.3 | Resolve all remaining compiler deprecation warnings | `mvn clean verify` passes with zero deprecation warnings |
| 0.4 | Run full test suite | All tests green |

### Phase 1 — Compilation (Spring Boot 4.0.x)

**Goal:** The application compiles against SB4.

| Step | Action | Item ID |
|------|--------|---------|
| 1.1 | Run OpenRewrite `UpgradeSpringBoot_4_0` recipe | Automated |
| 1.2 | Bump parent to `4.0.x` | — |
| 1.3 | Verify / fix starter artifact renames | C-02 |
| 1.4 | Replace `org.springframework.lang.NonNull` → `org.jspecify.annotations.NonNull` | C-01 |
| 1.5 | Add `jspecify` dependency if not transitively available | C-01 |
| 1.6 | Upgrade `maven-compiler-plugin` to ≥ 3.13.0 | C-05 |
| 1.7 | Upgrade `dependency-check-maven` to ≥ 11.x | C-06 |
| 1.8 | Upgrade `testcontainers.version` to ≥ 1.20.x | C-07 |
| 1.9 | Upgrade `azure-identity` to latest stable | C-08 |
| 1.10 | `mvn compile` succeeds | — |

### Phase 2 — Startup & Runtime

**Goal:** The application starts and handles requests correctly.

| Step | Action | Item ID |
|------|--------|---------|
| 2.1 | Fix renamed configuration properties | C-03, C-04 |
| 2.2 | Review `SecurityConfig` against Security 7 defaults; explicitly configure CSRF and session management if defaults changed | C-10 |
| 2.3 | Verify WebSocket/SockJS endpoint registration and error handler compile and function | C-11 |
| 2.4 | Verify Flyway `flyway-sqlserver` module compatibility with SB4-managed Flyway version | — |
| 2.5 | Start application with `local` profile — confirm H2 schema creation and seed data | C-13 |
| 2.6 | Start application with `prod` profile — confirm SQL Server connectivity and Flyway migration | — |

### Phase 3 — Behavioral Verification

**Goal:** No silent regressions in production behavior.

| Step | Test | Item ID |
|------|------|---------|
| 3.1 | Hit `/actuator/prometheus` — compare metric names against existing Prometheus scrape config | C-12 |
| 3.2 | Full WebSocket flow: login → join room → send message → receive broadcast → disconnect → LEAVE notification | C-11 |
| 3.3 | Admin flow: login as admin → list users → add user → delete user | C-10 |
| 3.4 | Security: unauthenticated access to protected endpoints returns 302 to `/login` | C-10 |
| 3.5 | Security: CSRF token present and enforced on form POST | C-10 |
| 3.6 | DataInitializer seeds admin/user1/user2 on fresh DB | C-13 |

### Phase 4 — CI/CD & Infrastructure

| Step | Action | Item ID |
|------|--------|---------|
| 4.1 | Update `azure-pipelines.yml` Maven version to 3.9.x+ | C-09 |
| 4.2 | Verify Dockerfile builds and runs (`docker build`, `docker run`) | — |
| 4.3 | Run `docker-compose up` — verify health endpoint responds | — |
| 4.4 | Run monitoring stack — verify Prometheus scrapes metrics successfully | C-12 |

### Phase 5 — Test Suite

| Step | Action |
|------|--------|
| 5.1 | Verify `spring-boot-starter-test` artifact (rename if needed, or use `-classic` bridge) |
| 5.2 | Verify `spring-security-test` compatibility with Security 7 |
| 5.3 | Run full test suite: `mvn clean verify -Ddependency-check.skip=true` |
| 5.4 | Fix any failing tests |

---

## 6  Dependency Version Matrix

| Dependency | Current | Target | Notes |
|------------|---------|--------|-------|
| `spring-boot-starter-parent` | 3.3.10 | 4.0.x | Core upgrade |
| `maven-compiler-plugin` | 3.11.0 | ≥ 3.13.0 | Build compatibility |
| `dependency-check-maven` | 8.4.0 | ≥ 11.x | Very outdated; NVD API changes |
| `testcontainers` | 1.19.7 | ≥ 1.20.x | JDK 21 + SB4 compat |
| `azure-identity` | 1.13.0 | Latest stable | Classpath compat |
| `jspecify` | N/A (new) | 1.0.0 | Required for `@NonNull` migration |
| Maven (CI) | 3.10.1 | ≥ 3.9.9 | Azure Pipelines |

> All Spring-managed transitive dependencies (Spring Security, Hibernate,
> Jackson, Flyway, Micrometer, H2, MSSQL JDBC) are controlled by the SB4
> parent BOM and do not require explicit version pinning.

---

## 7  Rollback Strategy

| Trigger | Action |
|---------|--------|
| Compilation failure after Phase 1 that cannot be resolved within 4 hours | Revert POM to `3.5.x`, continue with deprecation fixes |
| WebSocket behavioral regression in Phase 3 | Revert to `3.5.x`; open issue against Spring Framework 7 if regression confirmed |
| Flyway migration failure in production | Do **not** deploy; Flyway migrations are forward-only. Fix SQL or config before retry |
| Prometheus metric name changes break dashboards | Deploy with SB4 but update Grafana dashboards in parallel; no rollback needed |

---

## 8  Automation

### OpenRewrite Recipe

```bash
# Dry run (preview changes)
mvn rewrite:dryRun \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0

# Apply changes
mvn rewrite:run \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0
```

The recipe handles:
- Starter artifact renames
- Property key migrations
- Annotation import changes
- Deprecated API replacements

Manual review is still required after running the recipe.

---

## 9  Risk Register

| ID | Risk | Likelihood | Impact | Mitigation |
|----|------|------------|--------|------------|
| R-01 | SockJS/STOMP API changes in Spring Framework 7 break WebSocket flows | Medium | High | Full end-to-end WebSocket testing; SockJS fallback verification |
| R-02 | Spring Security 7 changes CSRF defaults, breaking form login or SockJS handshake | Medium | High | Explicit CSRF configuration; test all auth flows |
| R-03 | Jackson 3 serialization differences affect `ChatMessage` JSON payloads | Low | Medium | No custom Jackson code; auto-config handles it |
| R-04 | Hibernate dialect changes cause schema mismatch on SQL Server | Low | High | Test with `prod` profile against test SQL Server instance |
| R-05 | Actuator metric names change, breaking Prometheus/Grafana dashboards | Medium | Low | Compare `/actuator/prometheus` output before/after; update dashboards |
| R-06 | `dependency-check-maven` 11.x has different NVD API requirements | Low | Low | Update config in `pom.xml`; test with `NVD_API_KEY` |

---

## 10  Acceptance Criteria

The upgrade is considered complete when **all** of the following are true:

- [ ] `mvn clean verify` passes with zero compilation errors and zero test failures
- [ ] Application starts successfully with both `local` and `prod` profiles
- [ ] Full WebSocket chat flow works (join → message → disconnect → LEAVE broadcast)
- [ ] Spring Security login, logout, CSRF, and `@PreAuthorize` admin protection work
- [ ] DataInitializer creates seed users on fresh database
- [ ] `/actuator/health` returns `UP`
- [ ] `/actuator/prometheus` returns metrics and Prometheus can scrape them
- [ ] Flyway migration runs successfully against SQL Server
- [ ] Docker image builds and runs correctly
- [ ] Azure Pipelines CI build passes
- [ ] No `javax.*` or `org.springframework.lang.NonNull` imports remain in source

---

## 11  Estimated Effort

| Phase | Effort |
|-------|--------|
| Phase 0 — SB 3.5.x stepping stone | 1–2 hours |
| Phase 1 — Compilation fixes | 1–2 hours |
| Phase 2 — Startup/runtime fixes | 1–2 hours |
| Phase 3 — Behavioral verification | 2–3 hours |
| Phase 4 — CI/CD updates | 30 min |
| Phase 5 — Test suite | 1 hour |
| **Total** | **~1–2 days** |
