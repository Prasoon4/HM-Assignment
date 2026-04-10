# H&M Assignment App

An H&M Android assignment application developed by applicant **Prasoon Katiyar**[engg.prasoon@gmail.com]. Built with modern Android development tools following a clean architecture approach.

---

## Architecture

The project follows **Clean Architecture** with a clear separation of concerns across three layers:

```
presentation/   → Jetpack Compose UI, ViewModel, UI models
domain/         → Use cases, domain models, repository interface
data/           → API service (Ktor), remote DTOs, repository implementation
```

### Key Design Decisions

| Concern | Solution |
|---|---|
| UI framework | Jetpack Compose + Material 3 |
| State management | `ViewModel` with Compose `mutableStateOf` |
| Dependency injection | Hilt |
| Networking | Ktor client (Android engine) |
| JSON serialisation | Kotlinx Serialization |
| Image loading | Coil |
| Unit testing | JUnit 5 Jupiter + JUnit 4 Vintage Engine + MockK + Kotlin Coroutines Test |
| UI testing | Compose UI Test + Robolectric + MockK Android |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11
- Android SDK – minimum API 24, target API 36


---