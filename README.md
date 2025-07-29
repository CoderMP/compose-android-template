# 🚀 Compose Android Template
[![Static Badge](https://img.shields.io/badge/Using%20This%20Template-white?style=flat&logo=github&logoColor=%23181717&logoSize=auto&label=Create&labelColor=%23FFFFFF&color=%23238637)](https://github.com/CoderMP/compose-android-template/generate) [![Android CI](https://github.com/CoderMP/compose-android-template/actions/workflows/android-ci.yml/badge.svg)](https://github.com/CoderMP/compose-android-template/actions/workflows/android-ci.yml) ![Static Badge](https://img.shields.io/badge/100%25-blue?style=flat&logo=kotlin&logoColor=%237F52FF&logoSize=auto&label=Kotlin&labelColor=%23272C33&color=%230273B4) ![Static Badge](https://img.shields.io/badge/MIT-blue?style=flat&logo=thestorygraph&logoColor=%23FFFFFF&logoSize=auto&label=License&labelColor=%23272C33&color=%230273B4)

A modern, production-ready Android template built with **Jetpack Compose** and **Kotlin**. Get your next Android project up and running in seconds with industry best practices baked in.

## ✨ What's Inside
- **🎨 Jetpack Compose** - Modern declarative UI toolkit
- **🏗️ Clean Architecture** - Organized with proper separation of concerns
- **💉 Koin** - Lightweight dependency injection
- **🧭 Navigation Component** - Type-safe navigation with Kotlin Serialization
- **🎯 Result Pattern** - Robust error handling with custom Result types
- **🌙 Material 3** - Latest Material Design with dynamic theming support
- **🔧 Gradle Kotlin DSL** - Modern build configuration
- **🚀 GitHub Actions** - Automated CI/CD pipeline
- **📱 Template Cleanup** - Automatic package renaming and setup

## 🏃‍♂️ Quick Start
1. **Use this template** by clicking the "Use this template" button
2. **Name your project** and wait for the automatic cleanup to complete
3. **Start building** your amazing Android app!

The template cleanup workflow will automatically:
- ✅ Rename packages to match your project
- ✅ Update app name and configurations
- ✅ Remove template-specific files
- ✅ Set up your project structure

## 🏛️ Architecture
The architecture of the project is based off of a modular approach for the package structure, and enforces MVI-based layering within these packages. This allows you to focus on properly structuring your code in a clean manner, while having the flexibility to "export" these packages as modules as the project scales up in size.
```
app/
├── 📱 app/                 # Application layer (DI, navigation)
		├── di/            # Dependency injection modules
		├── navgiation/    # Navigation logic
├── 🏗️ core/
    ├── di/            # Business logic & models
		├── domain/            # Business logic & models
    └── presentation/      # UI components & theming
```

## 🛠️ Built With
- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **DI**: [Koin](https://insert-koin.io/)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
    - Note: once a stable release of Compose Navigation 3 is released, the project will be updated accordingly
- **Logging**: [Timber](https://github.com/JakeWharton/timber)
- **Build**: [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

## 🤝 Contributing
Found a bug or have an improvement? Feel free to open an issue or submit a pull request!

## 📄 License
This template is available under the MIT License. See the [LICENSE](LICENSE) file for more info.

---

**Happy coding!** 🎉 If this template helped you, consider giving it a ⭐️

