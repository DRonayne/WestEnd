<div align="center">
  <img src="https://github.com/user-attachments/assets/26586094-6b53-4f27-9a6e-efef3f1bf1df" alt="ic_launcher" width="200">
  
  <h1>West End Shows 🎭 - Android App</h1>
</div>

<div align="center">
  <table>
    <tr>
      <td align="center"><strong>Phone</strong></td>
      <td align="center"><strong>Tablet</strong></td>
    </tr>
    <tr>
      <td>
        <video src="https://github.com/user-attachments/assets/a24b2e19-b094-4438-bd9f-6b8e455bf4c5" 
               controls muted width="250"
               alt="Phone Demo">
        </video>
      </td>
      <td>
        <video src="https://github.com/user-attachments/assets/6d1c4fc3-f2f5-4f3b-a9ed-0992875c028e" 
               controls muted width="250"
               alt="Tablet Demo">
        </video>
      </td>
    </tr>
  </table>
</div>

## 🎯 About

A modern Android application that brings London's vibrant theatre scene to your device. Built with the latest Android technologies and Material Design 3 principles, it offers a seamless experience for discovering West End performances.

## 🏗️ Architecture

```kotlin
.
├── data                    # Data layer
│   ├── local              # Local storage
│   │   ├── dao           # Data Access Objects
│   │   └── entity        # Database entities
│   └── remote            # Remote data source
│       ├── api           # API interfaces
│       └── dto           # Data Transfer Objects
├── di                     # Dependency injection modules
├── domain                 # Business logic
│   ├── model             # Domain models
│   └── repository        # Repository interfaces
├── presentation          # UI layer
│   ├── home             # Home screen
│   ├── navigation       # Navigation management
│   ├── saved            # Saved shows feature
│   ├── search           # Search functionality
│   ├── settings         # App settings
│   ├── show             # Show details
│   └── theme            # App theming
└── utils                # Utility functions
```

### Tech Stack
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Concurrency**: Kotlin Coroutines & Flow
- **Storage**: Room
- **Networking**: Retrofit
- **Image Loading**: Coil
- **Maps**: OpenStreetMap
- **Testing**: JUnit5, Turbine, MockK

### 🛠️ Core Features

#### Show Browsing
- **Content Organisation**
  - Home feed with categorised sections
  - Award and popularity flags in data model
  - New show detection with timestamp tracking
- **Search Implementation**
  - Multi-parameter filtering system
  - Price range slider

#### 📱 UI Architecture
- Responsive Compose layouts for larger screens
- Edge-to-edge implementation with insets handling
- Material You integration with dynamic colour extraction
- Rail/bottom nav pattern based on screen size

#### 🔄 Core Functionality
- OpenStreetMap integration for venue location maps
- Native share sheet implementation 
- Favourites system with local persistence
- Offline caching
