# TVManiak 📺

TVManiak is a Kotlin Multiplatform application for tracking and discovering TV shows across Android, iOS, and Desktop platforms. Built with Compose Multiplatform and following clean architecture principles.

## 🌟 Features

- **TV Show Discovery**: Browse and search through a vast collection of TV shows
- **Advanced Search**: Find shows with intelligent scoring and sorting by relevance
- **Show Details**: View comprehensive information about your favorite shows
- **Watchlist**: Keep track of shows you want to watch
- **Cross-Platform**: Runs on Android, iOS, and Desktop (JVM)

## 🏗️ Architecture

The project follows a modular clean architecture with clear separation of concerns:

### Module Structure
- **composeApp**: Main application module with platform-specific entry points
- **feature/**: Feature modules containing screens and presentation logic
  - `tvShows`: TV shows listing and search functionality
  - `tvShowDetails`: Show details screen
  - `watchlist`: User's watchlist management
- **core/**: Core modules providing shared functionality
  - `data/`: Data layer with repository implementations, local/remote data sources
  - `domain`: Business logic and use cases
  - `model`: Data models and entities
  - `common`: Shared utilities and extensions
  - `designsystem`: Shared UI components and theming

### Tech Stack
- **UI**: Compose Multiplatform with Material3 design
- **Architecture**: Clean Architecture with MVVM pattern
- **DI**: Koin for dependency injection
- **Networking**: Ktor for HTTP client with custom interceptors
- **Database**: SQLDelight for local persistence
- **Navigation**: Compose Navigation with shared transitions
- **Image Loading**: Coil3 with Ktor integration and caching
- **Testing**: Kotlin Test with coroutines support

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Xcode (for iOS development)
- JDK 11 or higher

### iOS Setup

If you encounter SQLite linking errors on iOS, you may need to manually link libsqlite3:

1. Open your Xcode project
2. Navigate to **Project Settings** > **Build Phases** > **Link Binary with Libraries**
3. Click the "+" button and add `libsqlite3.tbd` to your target

### Development Commands

#### Building
```bash
# Build entire project
./gradlew build

# Assemble Android app
./gradlew assembleDebug
./gradlew assembleRelease

# Build for iOS
./gradlew linkDebugFrameworkIosArm64
```

#### Running
```bash
# Run Android app
./gradlew installDebug

# Run Desktop (JVM) app
./gradlew :composeApp:run

# iOS requires Xcode or running through Android Studio
```

#### Testing
```bash
# Run all tests
./gradlew allTests

# Run platform-specific tests
./gradlew testDebugUnitTest  # Android tests
./gradlew jvmTest           # JVM tests
./gradlew iosX64Test        # iOS tests
```

#### Code Quality
```bash
# Run ktlint checks
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat

# Run detekt static analysis
./gradlew detekt
```

## 📱 Platform Support

- **Android**: Material3 design with adaptive layouts
- **iOS**: Native iOS integration with SwiftUI compatibility
- **Desktop**: Full-featured desktop experience with keyboard navigation

## 🔧 Development Features

- **Hot Reload**: Fast development with Compose Multiplatform hot reload
- **Modular Architecture**: Easy to maintain and scale
- **Dependency Injection**: Clean and testable code with Koin
- **Error Handling**: Comprehensive error handling with proper user feedback
- **Image Caching**: Efficient image loading with memory and disk caching
- **Search Optimization**: Advanced search with scoring and result persistence

## 📊 Data Sources

- **TVMaze API**: Primary data source for TV show information
- **Local Database**: SQLDelight for offline capabilities and caching
- **Image CDN**: Optimized image delivery with multiple resolution support

## 🧪 Testing

The project includes comprehensive testing at multiple levels:
- **Unit Tests**: Business logic and use cases
- **Integration Tests**: Repository and data layer testing
- **UI Tests**: Screen and component testing

## 📄 Project Structure

```
TVManiak/
├── composeApp/           # Main application module
├── core/
│   ├── data/            # Data layer (local, remote, repository)
│   ├── domain/          # Business logic and use cases
│   ├── model/           # Data models
│   ├── common/          # Shared utilities
│   └── designsystem/    # UI components and theming
├── feature/
│   ├── tvShows/         # TV shows listing and search
│   ├── tvShowDetails/   # Show details screen
│   └── watchlist/       # Watchlist management
└── build-logic/         # Build configuration and convention plugins
```

## 🤝 Contributing

1. Follow the existing code style and architecture patterns
2. Add tests for new functionality
3. Use the provided Gradle tasks for code quality checks
4. Follow conventional commits for commit messages

## 📚 Learn More

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- [TVMaze API](https://www.tvmaze.com/api)