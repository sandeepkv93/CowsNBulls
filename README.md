# 🐮 Bulls & Cows - Android Game

A modern Android implementation of the classic Bulls & Cows number guessing game, built with Jetpack Compose and Material 3.

## 🎮 Features

- **VS Computer Mode** with AI opponent
  - 3 difficulty levels: Easy, Medium, Hard
  - Smart AI using candidate elimination algorithm
- **Smooth Gameplay**
  - Conditional auto-scrolling for guess history
  - Spring-based animations
  - Haptic feedback
- **Customizable Settings**
  - Configurable digit count (3-6 digits)
  - Toggle digit repetition
  - Toggle leading zeros
  - Settings persistence with SharedPreferences
- **Victory Screen** with animated stats and elapsed time
- **Material 3 UI** with modern design and smooth transitions

## 🛠️ Tech Stack

- **Kotlin** - Primary language
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Design system
- **MVVM Architecture** - ViewModel + StateFlow
- **Kotlin Coroutines** - Asynchronous operations
- **Navigation Compose** - Type-safe navigation
- **JUnit & Coroutines Test** - Unit testing (53 comprehensive tests)

## 📦 Installation

1. Clone the repository:
```bash
git clone https://github.com/sandeepkv93/CowsNBulls.git
cd CowsNBulls
```

2. Open the project in Android Studio (Hedgehog or later)

3. Sync Gradle and build the project:
```bash
./gradlew build
```

4. Run on an emulator or physical device:
```bash
./gradlew installDebug
```

## 🎯 How to Play

1. **Select Difficulty**: Choose Easy, Medium, or Hard AI opponent
2. **Customize Rules** (optional): Adjust settings from the home screen
3. **Make Guesses**: Enter digits using the on-screen keypad
4. **Get Feedback**:
   - 🐮 **Bulls**: Correct digit in correct position
   - 🐄 **Cows**: Correct digit in wrong position
5. **Win**: Match all digits to crack the code!

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

**Test Coverage:**
- GameEngine: 22 tests (bulls/cows calculation, validation, secret generation)
- AIOpponent: 12 tests (all difficulty levels, candidate filtering)
- GameViewModel: 19 tests (state management, coroutines)

## 📂 Project Structure

```
app/src/main/java/com/example/cowsnbulls/
├── domain/
│   ├── engine/         # Game logic & AI
│   └── model/          # Data models
├── ui/
│   ├── components/     # Reusable UI components
│   ├── screens/        # App screens
│   ├── navigation/     # Navigation setup
│   └── theme/          # Material 3 theme
├── viewmodel/          # ViewModels
├── data/local/         # Settings persistence
└── util/               # Utility classes
```

## 🚀 Future Enhancements

- Firebase multiplayer mode (Play with Friend)
- Game statistics and leaderboards
- Sound effects
- Multiple theme options

## 👤 Author

**Sandeep Vishnu**

## 📄 License

This project is open source and available under the MIT License.
