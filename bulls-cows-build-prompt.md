# Bulls & Cows Android App - Complete Build Specification

## Project Overview
Build a premium Android game app called "Bulls & Cows" (also known as "Mastermind with numbers") using **Jetpack Compose**, **Kotlin**, and **Firebase** for backend. The app should have a polished, modern UI with smooth animations and two core game modes: **VS Computer** and **Play with Friend** (remote multiplayer).

---

## Core Game Rules

### Basic Gameplay
1. **Secret Code**: One player creates a secret code of N digits (default 4)
2. **Unique Digits**: All digits in the secret must be unique (no repeats)
3. **No Leading Zero**: The first digit cannot be 0
4. **Guessing**: The other player tries to guess the secret code
5. **Feedback After Each Guess**:
   - **Bulls (🐂)**: Number of digits that are correct AND in the correct position
   - **Cows (🐮)**: Number of digits that are correct but in the WRONG position
6. **Win Condition**: Guess all digits correctly (N bulls, 0 cows)
7. **Goal**: Solve in minimum attempts and fastest time

### Configurable Difficulty Settings
Allow users to customize:
- **Number of Digits**: 3, 4, 5, or 6 digits (default: 4)
- **Allow Repeated Digits**: ON/OFF (default: OFF)
- **Allow Leading Zero**: ON/OFF (default: OFF)

---

## Game Modes

### Mode 1: VS Computer (AI)
**Flow**:
1. User selects difficulty level: **Easy**, **Medium**, or **Hard**
2. User selects game settings (digits, repeats, leading zero)
3. Computer generates a random secret code following the rules
4. User makes guesses via custom keypad
5. After each guess, show bulls/cows feedback
6. Track attempts, time, and accuracy
7. On win: show victory screen with stats
8. Option to play again or return home

**AI Difficulty Levels** (Computer as Opponent):
- **Easy**: Random valid guesses, occasionally smart
- **Medium**: Maintains candidate set, eliminates impossibilities
- **Hard**: Uses information theory (entropy-based guessing) to minimize search space

**Implementation Details**:
- Computer secret generation: 
  ```kotlin
  // Generate valid secret based on settings
  // e.g., for 4 unique digits, no leading zero: 1234, 5678, 9021, etc.
  ```
- Validation: Check guess against secret, calculate bulls/cows
- Guess history: Store all guesses with feedback

---

### Mode 2: Play with Friend (Remote Multiplayer)
**Flow - Turn-Based Async**:
1. **Host**: User creates a game room, sets difficulty settings
2. **Invite**: Share room code or send invite link
3. **Join**: Friend enters room code to join
4. **Setup Phase**:
   - Both players create their own secret codes
   - Codes are submitted to Firebase (stored securely)
5. **Gameplay**:
   - Players take turns guessing each other's secrets
   - Turn indicator shows whose turn it is
   - Real-time updates when opponent makes a move
   - Push notifications when it's your turn (optional for v1)
6. **Win Condition**: First player to solve opponent's secret wins
7. **Post-Game**: Show winner, stats comparison, rematch option

**Implementation Details**:
- **Firebase Realtime Database** for match state
- Match data structure:
  ```
  matches/{matchId}:
    - matchId: string
    - hostId: string
    - guestId: string (null until joined)
    - settings: { digits, allowRepeats, allowLeadingZero }
    - state: "waiting" | "setup" | "active" | "finished"
    - currentTurn: "host" | "guest"
    - secrets: {
        host: string (hashed or encrypted),
        guest: string (hashed or encrypted)
      }
    - guesses: [
        { playerId, guess, bulls, cows, timestamp }
      ]
    - winner: null | "host" | "guest"
    - createdAt: timestamp
    - updatedAt: timestamp
  ```
- **Room Code**: Generate 6-character alphanumeric code (e.g., "A7X9K2")
- **Security**: Never expose opponent's secret to client; validate server-side
- **Listeners**: Use Firebase listeners for real-time updates

---

## UI/UX Design Specification

### Design Principles
- **Premium Feel**: Glassmorphism, soft gradients, smooth animations
- **Color Scheme**: 
  - Primary: Purple gradient (`#667eea` to `#764ba2`)
  - Bulls (correct): Green gradient (`#4ade80` to `#22c55e`)
  - Cows (near miss): Amber gradient (`#fbbf24` to `#f59e0b`)
  - Background: Light mode with subtle gradients
- **Typography**: 
  - Headers: Bold, 700-800 weight
  - Body: Regular, 400-600 weight
  - Numbers: Monospace or tabular for alignment
- **Spacing**: Generous padding (16-32dp), clean breathing room
- **Animations**: Spring-based (Compose `spring()` spec), snappy but not jarring

---

## Screen-by-Screen Breakdown

### 1. Home Screen
**Layout**:
```
┌─────────────────────────────┐
│      🎮 Bulls & Cows       │
│   Crack the code, win!      │
├─────────────────────────────┤
│                             │
│  ┌─────────────────────┐   │
│  │   🤖 VS Computer    │   │
│  │   Challenge AI      │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │   👥 Play with      │   │
│  │      Friend         │   │
│  │   Battle remotely   │   │
│  └─────────────────────┘   │
│                             │
├─────────────────────────────┤
│  Rating: 1847  Streak: 24  │
└─────────────────────────────┘
```

**Components**:
- **Title**: Large, gradient text "Bulls & Cows"
- **Subtitle**: Small gray text
- **Mode Cards**: Two cards with:
  - Emoji icon (48sp)
  - Title (20sp, bold)
  - Description (14sp, gray)
  - Glassmorphic background with hover lift effect
  - onClick → navigate to respective mode
- **Stats Row**: Two stat cards showing user rating and streak (optional for v1)
- **Settings Icon** (top-right): Opens settings sheet

**Compose Code Structure**:
```kotlin
@Composable
fun HomeScreen(
    onVsComputerClick: () -> Unit,
    onPlayWithFriendClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(...))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text("Bulls & Cows", style = MaterialTheme.typography.displayLarge)
        
        // Mode Cards
        ModeCard(
            icon = "🤖",
            title = "VS Computer",
            description = "Challenge AI",
            onClick = onVsComputerClick
        )
        
        ModeCard(
            icon = "👥",
            title = "Play with Friend",
            description = "Battle remotely",
            onClick = onPlayWithFriendClick
        )
        
        // Stats (optional)
        StatsRow(rating = 1847, streak = 24)
    }
}
```

---

### 2. Difficulty Selection Screen (VS Computer)
**Layout**:
```
┌─────────────────────────────┐
│  ← Choose Difficulty        │
├─────────────────────────────┤
│                             │
│  ┌─────────────────────┐   │
│  │   😊 Easy           │   │
│  │   Casual play       │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │   😐 Medium         │   │
│  │   Balanced          │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │   😈 Hard           │   │
│  │   Expert AI         │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │   ⚙️ Custom Rules   │   │
│  └─────────────────────┘   │
└─────────────────────────────┘
```

**Components**:
- **Back Button**: Top-left, returns to home
- **Title**: "Choose Difficulty"
- **Difficulty Cards**: Three cards for Easy/Medium/Hard
  - onClick → navigate to game screen with selected difficulty
- **Custom Rules Button**: Opens bottom sheet with:
  - Slider/Selector for number of digits (3-6)
  - Toggle for "Allow repeated digits"
  - Toggle for "Allow leading zero"
  - "Start Game" button

---

### 3. Multiplayer Setup Screen
**Layout**:
```
┌─────────────────────────────┐
│  ← Play with Friend         │
├─────────────────────────────┤
│                             │
│  ┌─────────────────────┐   │
│  │   🏠 Create Room    │   │
│  │   Host a game       │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │   🔗 Join Room      │   │
│  │   Enter code        │   │
│  └─────────────────────┘   │
│                             │
└─────────────────────────────┘
```

**Create Room Flow**:
1. User clicks "Create Room"
2. Select custom rules (or use defaults)
3. Firebase creates match document with unique matchId
4. Generate 6-char room code (e.g., "A7X9K2")
5. Show "Waiting for Friend" screen:
   ```
   ┌─────────────────────────────┐
   │  Waiting for Friend...      │
   ├─────────────────────────────┤
   │                             │
   │      Room Code:             │
   │      ┌──────────┐           │
   │      │  A7X9K2  │           │
   │      └──────────┘           │
   │                             │
   │  [Share Code] [Copy]        │
   │                             │
   │  [Cancel]                   │
   └─────────────────────────────┘
   ```
6. When friend joins → navigate to secret setup screen

**Join Room Flow**:
1. User clicks "Join Room"
2. Show input screen:
   ```
   ┌─────────────────────────────┐
   │  ← Enter Room Code          │
   ├─────────────────────────────┤
   │                             │
   │  Room Code                  │
   │  ┌─────────────────────┐   │
   │  │ [____][____][____]  │   │
   │  │ [____][____][____]  │   │
   │  └─────────────────────┘   │
   │                             │
   │  [Join Game]                │
   │                             │
   └─────────────────────────────┘
   ```
3. Validate code with Firebase
4. If valid, update match document with guestId
5. Navigate to secret setup screen

---

### 4. Secret Setup Screen (Multiplayer Only)
**Layout**:
```
┌─────────────────────────────┐
│  Create Your Secret Code    │
├─────────────────────────────┤
│                             │
│  Your Secret (4 digits):    │
│  ┌───┐ ┌───┐ ┌───┐ ┌───┐  │
│  │ 5 │ │ 6 │ │ 7 │ │ 8 │  │
│  └───┘ └───┘ └───┘ └───┘  │
│                             │
│  ┌─────────────────────┐   │
│  │  0  1  2  3  4      │   │
│  │  5  6  7  8  9      │   │
│  └─────────────────────┘   │
│                             │
│  [Clear]  [Submit Secret]   │
│                             │
│  Opponent: Creating...      │
└─────────────────────────────┘
```

**Flow**:
1. Both players see this screen simultaneously
2. Enter N digits using custom keypad
3. Validation: 
   - All digits unique (if repeats disabled)
   - First digit not 0 (if leading zero disabled)
   - Exactly N digits
4. On "Submit Secret":
   - Hash/encrypt secret and send to Firebase
   - Show "Waiting for opponent..." indicator
5. When both players submit → navigate to game board

---

### 5. Game Board Screen (Core Gameplay)
**Layout**:
```
┌─────────────────────────────┐
│  ← [Timer: 02:47]           │
├─────────────────────────────┤
│  ┌─────────────────────┐   │
│  │ 🤖 AI Master        │   │ ← Opponent Panel
│  │ Thinking...         │   │
│  └─────────────────────┘   │
├─────────────────────────────┤
│  Guess History:             │
│                             │
│  #1  [1][2][3][4]  🐂1 🐮1 │
│  #2  [5][6][2][1]  🐂2 🐮1 │
│  #3  [5][6][7][8]  🐂3 🐮0 │
│                             │
├─────────────────────────────┤
│  Your Guess #4:             │
│  ┌───┐ ┌───┐ ┌───┐ ┌───┐  │
│  │ 5 │ │ 6 │ │ 7 │ │ ? │  │
│  └───┘ └───┘ └───┘ └───┘  │
│                             │
│  ┌─────────────────────┐   │
│  │  0  1  2  3  4      │   │
│  │  5  6  7  8  9      │   │
│  └─────────────────────┘   │
│                             │
│  [Clear]  [Submit Guess]    │
└─────────────────────────────┘
```

**Components in Detail**:

#### A. Opponent Panel (Top)
- **Avatar**: Circle with emoji or initials
- **Name**: "AI Master" or friend's name
- **Status Indicator**:
  - VS Computer: "Thinking..." (pulsing dot)
  - Multiplayer: 
    - "Your turn" (green dot)
    - "Opponent's turn" (orange dot)
    - "Typing..." (animated)

#### B. Guess History (Scrollable List)
- Each row contains:
  - **Guess Number**: #1, #2, #3...
  - **Digit Slots**: 4 squares showing the guess
  - **Feedback Pills**:
    - 🐂 Bulls: Green pill with count (e.g., "🐂 2")
    - 🐮 Cows: Amber pill with count (e.g., "🐮 1")
- **Animation**: New guess slides in from left with spring animation
- **Styling**: 
  - Latest guess has subtle highlight
  - Previous guesses slightly dimmed

#### C. Current Guess Input
- **Label**: "Your Guess #N"
- **Digit Slots**: 4-6 large squares (based on settings)
  - Empty slots show "?"
  - Filled slots show digit with purple gradient background
  - Border animates on focus
- **Keypad**: Custom 0-9 keypad
  - 2 rows: 0-4 on top, 5-9 on bottom
  - Each key is a rounded square
  - **Used Digits** (if unique mode): 
    - Dim opacity to 0.3
    - Disable interaction
  - **Active Keys**: Haptic feedback on press
  - **Animation**: Scale down on press (0.95x)

#### D. Action Buttons
- **Clear**: Secondary style, clears current guess
- **Submit Guess**: Primary gradient button
  - Disabled if guess incomplete
  - onClick:
    - Validate guess
    - Calculate bulls/cows
    - Add to guess history
    - Check win condition
    - In multiplayer: update Firebase, switch turn

**Compose Code Structure**:
```kotlin
@Composable
fun GameBoardScreen(
    gameState: GameState,
    onGuessSubmit: (String) -> Unit,
    onClear: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = { Text("Timer: ${gameState.elapsedTime}") },
            navigationIcon = { BackButton(onClick = onBackClick) }
        )
        
        // Opponent Panel
        OpponentPanel(opponent = gameState.opponent)
        
        // Guess History
        LazyColumn {
            items(gameState.guessHistory) { guess ->
                GuessRow(guess = guess)
            }
        }
        
        // Current Guess
        CurrentGuessInput(
            currentGuess = gameState.currentGuess,
            digitCount = gameState.settings.digits
        )
        
        // Keypad
        CustomKeypad(
            usedDigits = gameState.usedDigits,
            onDigitClick = { digit -> 
                // Add digit to currentGuess
            }
        )
        
        // Actions
        Row {
            Button(onClick = onClear) { Text("Clear") }
            Button(
                onClick = { onGuessSubmit(gameState.currentGuess) },
                enabled = gameState.currentGuess.length == gameState.settings.digits
            ) {
                Text("Submit Guess")
            }
        }
    }
}

@Composable
fun GuessRow(guess: Guess) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Guess number
        Text("#${guess.number}", color = Color.Gray)
        
        // Digit slots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            guess.digits.forEach { digit ->
                DigitSlot(digit = digit, filled = true)
            }
        }
        
        // Feedback
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeedbackPill(
                icon = "🐂",
                count = guess.bulls,
                color = Color.Green
            )
            FeedbackPill(
                icon = "🐮",
                count = guess.cows,
                color = Color.Amber
            )
        }
    }
}

@Composable
fun DigitSlot(digit: String, filled: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp, 56.dp)
            .background(
                brush = if (filled) purpleGradient else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, Color.LightGray, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (filled) digit else "?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FeedbackPill(icon: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Text(count.toString(), fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun CustomKeypad(
    usedDigits: Set<String>,
    onDigitClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 0-4
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (0..4).forEach { digit ->
                KeypadButton(
                    digit = digit.toString(),
                    enabled = digit.toString() !in usedDigits,
                    onClick = { onDigitClick(digit.toString()) }
                )
            }
        }
        
        // Row 2: 5-9
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (5..9).forEach { digit ->
                KeypadButton(
                    digit = digit.toString(),
                    enabled = digit.toString() !in usedDigits,
                    onClick = { onDigitClick(digit.toString()) }
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    digit: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                color = if (enabled) Color.White else Color.LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) {
                // Haptic feedback
                onClick()
            }
            .scale(if (enabled) 1f else 0.95f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color.Black else Color.Gray
        )
    }
}
```

---

### 6. Victory Screen
**Layout**:
```
┌─────────────────────────────┐
│         🏆                  │
│       Victory!              │
├─────────────────────────────┤
│                             │
│  ┌──────┐  ┌──────┐        │
│  │  6   │  │ 2:47 │        │
│  │Guesses│ │ Time │        │
│  └──────┘  └──────┘        │
│                             │
│  ┌──────┐  ┌──────┐        │
│  │ +25  │  │ 94%  │        │
│  │Rating│  │ Acc. │        │
│  └──────┘  └──────┘        │
│                             │
│  [Play Again]               │
│  [Share Result]             │
│  [Home]                     │
│                             │
└─────────────────────────────┘
```

**Components**:
- **Trophy Animation**: Bouncing trophy emoji (🏆)
- **Victory Text**: "Victory!" or "You Win!" in large font
- **Stats Grid**: 2x2 grid showing:
  - Attempts: Number of guesses
  - Time: Elapsed time (mm:ss)
  - Rating: Points gained (multiplayer)
  - Accuracy: % of optimal play
- **Action Buttons**:
  - Play Again: Restart with same settings
  - Share Result: Share screenshot or text summary
  - Home: Return to home screen

**Animations**:
- Trophy bounces continuously
- Stats count up from 0 to final value (number ticker)
- Confetti particles (optional, use library like `compose-particle-system`)

---

### 7. Settings Screen (Accessible from Home)
**Layout**:
```
┌─────────────────────────────┐
│  ← Settings                 │
├─────────────────────────────┤
│                             │
│  Game Rules                 │
│  ┌─────────────────────┐   │
│  │ Number of Digits    │   │
│  │ [3] [4] [5] [6]     │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │ Allow Repeated     │   │
│  │ Digits        [ON] │   │
│  └─────────────────────┘   │
│                             │
│  ┌─────────────────────┐   │
│  │ Allow Leading      │   │
│  │ Zero          [OFF]│   │
│  └─────────────────────┘   │
│                             │
│  Appearance                 │
│  ┌─────────────────────┐   │
│  │ Theme              │   │
│  │ [Light] [Dark]     │   │
│  └─────────────────────┘   │
│                             │
│  [Save Settings]            │
└─────────────────────────────┘
```

**Features**:
- Save settings to local SharedPreferences or Room DB
- Apply globally or per-game mode

---

## Data Architecture

### Local State Management
Use **ViewModel** + **StateFlow** for each screen:

```kotlin
// GameViewModel.kt
class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    fun submitGuess(guess: String) {
        // Validate guess
        // Calculate bulls/cows
        // Update state
        // Check win condition
    }
    
    fun clearGuess() {
        _gameState.update { it.copy(currentGuess = "") }
    }
}

data class GameState(
    val mode: GameMode = GameMode.VS_COMPUTER,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val settings: GameSettings = GameSettings(),
    val secret: String = "",
    val currentGuess: String = "",
    val guessHistory: List<Guess> = emptyList(),
    val usedDigits: Set<String> = emptySet(),
    val elapsedTime: Long = 0,
    val isWon: Boolean = false,
    val opponent: Opponent = Opponent.AI
)

data class GameSettings(
    val digits: Int = 4,
    val allowRepeats: Boolean = false,
    val allowLeadingZero: Boolean = false
)

data class Guess(
    val number: Int,
    val digits: String,
    val bulls: Int,
    val cows: Int,
    val timestamp: Long
)

enum class GameMode { VS_COMPUTER, MULTIPLAYER }
enum class Difficulty { EASY, MEDIUM, HARD }
sealed class Opponent {
    object AI : Opponent()
    data class Player(val name: String, val avatar: String) : Opponent()
}
```

---

### Firebase Structure (Multiplayer)

**Realtime Database Schema**:
```json
{
  "matches": {
    "matchId_abc123": {
      "matchId": "abc123",
      "roomCode": "A7X9K2",
      "hostId": "user_1",
      "guestId": "user_2",
      "settings": {
        "digits": 4,
        "allowRepeats": false,
        "allowLeadingZero": false
      },
      "state": "active",
      "currentTurn": "host",
      "secrets": {
        "host": "hashed_secret_1",
        "guest": "hashed_secret_2"
      },
      "guesses": [
        {
          "playerId": "user_1",
          "guess": "1234",
          "bulls": 1,
          "cows": 2,
          "timestamp": 1700000000
        }
      ],
      "winner": null,
      "createdAt": 1700000000,
      "updatedAt": 1700000100
    }
  },
  "users": {
    "user_1": {
      "name": "Alice",
      "avatar": "🎮",
      "rating": 1847,
      "streak": 24
    }
  }
}
```

**Firebase Operations**:
```kotlin
// MultiplayerRepository.kt
class MultiplayerRepository(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    fun createMatch(settings: GameSettings): Flow<Match> {
        // Generate roomCode
        // Create match document
        // Return Flow<Match> for real-time updates
    }
    
    fun joinMatch(roomCode: String): Flow<Match> {
        // Find match by roomCode
        // Update guestId
        // Return Flow<Match>
    }
    
    fun submitSecret(matchId: String, secret: String) {
        // Hash secret
        // Update match/secrets/{playerId}
    }
    
    fun submitGuess(matchId: String, guess: String) {
        // Validate guess
        // Calculate bulls/cows (server-side if possible)
        // Add to guesses array
        // Switch turn
        // Check win condition
    }
    
    fun listenToMatch(matchId: String): Flow<Match> {
        return callbackFlow {
            val listener = database.reference
                .child("matches")
                .child(matchId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val match = snapshot.getValue(Match::class.java)
                        trySend(match)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                })
            awaitClose { database.reference.removeEventListener(listener) }
        }
    }
}
```

**Security Rules** (Firebase Realtime Database):
```json
{
  "rules": {
    "matches": {
      "$matchId": {
        ".read": "auth != null && (data.child('hostId').val() == auth.uid || data.child('guestId').val() == auth.uid)",
        ".write": "auth != null && (data.child('hostId').val() == auth.uid || data.child('guestId').val() == auth.uid)",
        "secrets": {
          ".read": "auth != null && (data.child('hostId').val() == auth.uid || data.child('guestId').val() == auth.uid)",
          ".write": "auth != null"
        }
      }
    }
  }
}
```

---

## Game Logic Implementation

### Bulls & Cows Calculator
```kotlin
// GameEngine.kt
object GameEngine {
    /**
     * Calculate bulls and cows for a guess against a secret
     * Bulls: correct digit in correct position
     * Cows: correct digit in wrong position
     */
    fun calculateFeedback(secret: String, guess: String): Pair<Int, Int> {
        require(secret.length == guess.length) { "Secret and guess must be same length" }
        
        var bulls = 0
        var cows = 0
        
        val secretChars = secret.toCharArray()
        val guessChars = guess.toCharArray()
        val matched = BooleanArray(secret.length) { false }
        
        // First pass: count bulls
        for (i in secretChars.indices) {
            if (secretChars[i] == guessChars[i]) {
                bulls++
                matched[i] = true
                guessChars[i] = 'X' // Mark as matched
            }
        }
        
        // Second pass: count cows
        for (i in guessChars.indices) {
            if (guessChars[i] != 'X') { // Not already matched as bull
                for (j in secretChars.indices) {
                    if (!matched[j] && secretChars[j] == guessChars[i]) {
                        cows++
                        matched[j] = true
                        break
                    }
                }
            }
        }
        
        return Pair(bulls, cows)
    }
    
    /**
     * Generate a random valid secret based on settings
     */
    fun generateSecret(settings: GameSettings): String {
        val digits = if (settings.allowRepeats) {
            (0..9).toList()
        } else {
            (0..9).shuffled().take(settings.digits)
        }
        
        val secret = digits.take(settings.digits).joinToString("")
        
        // Ensure no leading zero if disabled
        return if (!settings.allowLeadingZero && secret.startsWith("0")) {
            // Swap first digit with first non-zero digit
            val nonZeroIndex = secret.indexOfFirst { it != '0' }
            if (nonZeroIndex != -1) {
                secret.toCharArray().apply {
                    this[0] = this[nonZeroIndex].also { this[nonZeroIndex] = this[0] }
                }.joinToString("")
            } else {
                generateSecret(settings) // Retry
            }
        } else {
            secret
        }
    }
    
    /**
     * Validate a guess against game settings
     */
    fun validateGuess(guess: String, settings: GameSettings): ValidationResult {
        if (guess.length != settings.digits) {
            return ValidationResult.InvalidLength
        }
        
        if (!settings.allowRepeats && guess.toSet().size != guess.length) {
            return ValidationResult.RepeatedDigits
        }
        
        if (!settings.allowLeadingZero && guess.startsWith("0")) {
            return ValidationResult.LeadingZero
        }
        
        if (!guess.all { it.isDigit() }) {
            return ValidationResult.NonNumeric
        }
        
        return ValidationResult.Valid
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    object InvalidLength : ValidationResult()
    object RepeatedDigits : ValidationResult()
    object LeadingZero : ValidationResult()
    object NonNumeric : ValidationResult()
}
```

---

### AI Opponent Logic

```kotlin
// AIOpponent.kt
class AIOpponent(private val difficulty: Difficulty) {
    private var candidateSet: MutableSet<String> = mutableSetOf()
    
    fun initializeCandidates(settings: GameSettings) {
        candidateSet = generateAllValidSecrets(settings).toMutableSet()
    }
    
    fun makeGuess(settings: GameSettings, history: List<Guess>): String {
        return when (difficulty) {
            Difficulty.EASY -> makeRandomGuess()
            Difficulty.MEDIUM -> makeSmartGuess(history)
            Difficulty.HARD -> makeOptimalGuess(history)
        }
    }
    
    private fun makeRandomGuess(): String {
        return candidateSet.random()
    }
    
    private fun makeSmartGuess(history: List<Guess>): String {
        // Filter candidates based on previous feedback
        history.forEach { guess ->
            candidateSet.removeAll { candidate ->
                val (bulls, cows) = GameEngine.calculateFeedback(candidate, guess.digits)
                bulls != guess.bulls || cows != guess.cows
            }
        }
        return candidateSet.randomOrNull() ?: makeRandomGuess()
    }
    
    private fun makeOptimalGuess(history: List<Guess>): String {
        // Use entropy-based approach (Knuth's algorithm)
        // For each possible guess, calculate expected information gain
        // Choose guess that minimizes maximum remaining candidates
        
        makeSmartGuess(history) // Filter first
        
        if (candidateSet.size <= 2) {
            return candidateSet.first()
        }
        
        // Simplified: just pick from remaining candidates
        // For true optimal, implement minimax algorithm
        return candidateSet.random()
    }
    
    private fun generateAllValidSecrets(settings: GameSettings): List<String> {
        val digits = 0..9
        val secrets = mutableListOf<String>()
        
        fun backtrack(current: String) {
            if (current.length == settings.digits) {
                if (GameEngine.validateGuess(current, settings) == ValidationResult.Valid) {
                    secrets.add(current)
                }
                return
            }
            
            for (digit in digits) {
                if (!settings.allowRepeats && current.contains(digit.toString())) continue
                backtrack(current + digit)
            }
        }
        
        backtrack("")
        return secrets
    }
}
```

---

## Navigation Architecture

Use **Jetpack Navigation Compose**:

```kotlin
// Navigation.kt
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DifficultySelect : Screen("difficulty_select")
    object MultiplayerSetup : Screen("multiplayer_setup")
    object CreateRoom : Screen("create_room")
    object JoinRoom : Screen("join_room")
    object WaitingRoom : Screen("waiting_room/{matchId}")
    object SecretSetup : Screen("secret_setup/{matchId}")
    object GameBoard : Screen("game_board/{matchId?}")
    object Victory : Screen("victory")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onVsComputerClick = { navController.navigate(Screen.DifficultySelect.route) },
                onPlayWithFriendClick = { navController.navigate(Screen.MultiplayerSetup.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.DifficultySelect.route) {
            DifficultySelectScreen(
                onDifficultySelected = { difficulty, settings ->
                    // Start VS Computer game
                    navController.navigate(Screen.GameBoard.route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MultiplayerSetup.route) {
            MultiplayerSetupScreen(
                onCreateRoomClick = { navController.navigate(Screen.CreateRoom.route) },
                onJoinRoomClick = { navController.navigate(Screen.JoinRoom.route) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.GameBoard.route) {
            GameBoardScreen(
                onGameWon = { navController.navigate(Screen.Victory.route) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Victory.route) {
            VictoryScreen(
                onPlayAgainClick = { 
                    navController.popBackStack(Screen.Home.route, false)
                    navController.navigate(Screen.DifficultySelect.route)
                },
                onHomeClick = { 
                    navController.popBackStack(Screen.Home.route, false) 
                }
            )
        }
        
        // Add other screens...
    }
}
```

---

## Animation Specifications

### Screen Transitions
```kotlin
// Use slideInHorizontally/slideOutHorizontally
val enterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
)

val exitTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
)
```

### Guess Row Animation
```kotlin
@Composable
fun AnimatedGuessRow(guess: Guess) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    ) {
        GuessRow(guess)
    }
}
```

### Feedback Pill Animation
```kotlin
@Composable
fun AnimatedFeedbackPill(icon: String, count: Int, color: Color) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(200) // Delay after guess row appears
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        ) + fadeIn()
    ) {
        FeedbackPill(icon, count, color)
    }
}
```

### Keypad Button Press
```kotlin
@Composable
fun KeypadButton(digit: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clickable {
                pressed = true
                // Haptic feedback
                onClick()
                // Reset after delay
                pressed = false
            }
    ) {
        // Button content
    }
}
```

---

## Haptic Feedback

```kotlin
// HapticFeedback.kt
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember { view.hapticFeedback }
}

// Usage in keypad
val haptic = rememberHapticFeedback()

KeypadButton(
    digit = "5",
    onClick = {
        haptic.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        // Handle click
    }
)

// On correct guess
haptic.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

// On wrong guess
haptic.performHapticFeedback(HapticFeedbackConstants.REJECT)
```

---

## Sound Effects (Optional)

```kotlin
// SoundManager.kt
class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .build()
    
    private val keyTapSound = soundPool.load(context, R.raw.key_tap, 1)
    private val correctSound = soundPool.load(context, R.raw.correct, 1)
    private val wrongSound = soundPool.load(context, R.raw.wrong, 1)
    private val victorySound = soundPool.load(context, R.raw.victory, 1)
    
    fun playKeyTap() {
        soundPool.play(keyTapSound, 1f, 1f, 1, 0, 1f)
    }
    
    fun playCorrect() {
        soundPool.play(correctSound, 1f, 1f, 1, 0, 1f)
    }
    
    fun playWrong() {
        soundPool.play(wrongSound, 1f, 1f, 1, 0, 1f)
    }
    
    fun playVictory() {
        soundPool.play(victorySound, 1f, 1f, 1, 0, 1f)
    }
}
```

---

## Testing Strategy

### Unit Tests
```kotlin
// GameEngineTest.kt
class GameEngineTest {
    @Test
    fun `calculateFeedback returns correct bulls and cows`() {
        val secret = "1234"
        val guess = "1243"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)
        
        assertEquals(2, bulls) // 1 and 2 in correct positions
        assertEquals(2, cows)  // 4 and 3 in wrong positions
    }
    
    @Test
    fun `generateSecret creates valid secret`() {
        val settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = false)
        val secret = GameEngine.generateSecret(settings)
        
        assertEquals(4, secret.length)
        assertEquals(4, secret.toSet().size) // All unique
        assertNotEquals('0', secret[0]) // No leading zero
    }
    
    @Test
    fun `validateGuess detects repeated digits`() {
        val settings = GameSettings(digits = 4, allowRepeats = false)
        val result = GameEngine.validateGuess("1123", settings)
        
        assertEquals(ValidationResult.RepeatedDigits, result)
    }
}
```

### UI Tests
```kotlin
// GameBoardScreenTest.kt
@RunWith(AndroidJUnit4::class)
class GameBoardScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun submittingValidGuess_addsToHistory() {
        composeTestRule.setContent {
            GameBoardScreen(/* ... */)
        }
        
        // Enter guess
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("4").performClick()
        
        // Submit
        composeTestRule.onNodeWithText("Submit Guess").performClick()
        
        // Verify guess appears in history
        composeTestRule.onNodeWithText("#1").assertExists()
        composeTestRule.onNodeWithText("1234").assertExists()
    }
}
```

---

## Build Instructions

### Project Setup
1. **Create new Android Studio project**:
   - Template: Empty Compose Activity
   - Minimum SDK: 24 (Android 7.0)
   - Language: Kotlin
   - Build config: Kotlin DSL

2. **Add dependencies** (build.gradle.kts):
```kotlin
dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
}
```

3. **Firebase Setup**:
   - Create Firebase project at console.firebase.google.com
   - Add Android app to Firebase project
   - Download `google-services.json` to `app/` directory
   - Add Firebase plugin to build.gradle.kts:
     ```kotlin
     plugins {
         id("com.google.gms.google-services") version "4.4.0"
     }
     ```

4. **Project Structure**:
```
app/src/main/java/com/yourname/bullsandcows/
├── MainActivity.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── DifficultySelectScreen.kt
│   │   ├── MultiplayerSetupScreen.kt
│   │   ├── GameBoardScreen.kt
│   │   ├── VictoryScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/
│   │   ├── ModeCard.kt
│   │   ├── GuessRow.kt
│   │   ├── DigitSlot.kt
│   │   ├── FeedbackPill.kt
│   │   ├── CustomKeypad.kt
│   │   └── OpponentPanel.kt
│   └── navigation/
│       └── Navigation.kt
├── domain/
│   ├── model/
│   │   ├── GameState.kt
│   │   ├── GameSettings.kt
│   │   ├── Guess.kt
│   │   └── Match.kt
│   ├── engine/
│   │   ├── GameEngine.kt
│   │   └── AIOpponent.kt
│   └── repository/
│       ├── GameRepository.kt
│       └── MultiplayerRepository.kt
└── viewmodel/
    ├── GameViewModel.kt
    └── MultiplayerViewModel.kt
```

---

## Deployment Checklist

### Pre-Launch
- [ ] Test on multiple device sizes (phone, tablet)
- [ ] Test on Android 7.0+ (API 24+)
- [ ] Optimize images and assets
- [ ] Add ProGuard rules for release build
- [ ] Test offline mode (VS Computer)
- [ ] Test multiplayer with real network delays
- [ ] Add crash reporting (Firebase Crashlytics)
- [ ] Add analytics (Firebase Analytics)
- [ ] Create app icon and splash screen
- [ ] Write privacy policy (Firebase usage)

### Release Build
```kotlin
// build.gradle.kts (app)
android {
    signingConfigs {
        create("release") {
            storeFile = file("your-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Future Enhancements (Post-MVP)

### Phase 2
- Push notifications for multiplayer turn alerts
- Player profiles with avatars
- ELO-based ranking system
- Daily challenges with global leaderboard
- Achievements and badges
- Hints system (costs points)
- Themes and cosmetic unlocks

### Phase 3
- Tournament mode (bracket-style)
- Spectator mode (watch friends play)
- Replay/share game history
- Voice input for guesses
- Accessibility: TalkBack, large text, color blind mode
- Localization (multiple languages)

---

## Summary

This spec provides:
1. ✅ **Two game modes**: VS Computer and Play with Friend (remote)
2. ✅ **Configurable difficulty**: Number of digits, repeats, leading zero
3. ✅ **Premium UI**: Glassmorphism, gradients, smooth animations
4. ✅ **Beautiful feedback**: 🐂 Bulls and 🐮 Cows with emoji counts
5. ✅ **Firebase multiplayer**: Real-time turn-based gameplay
6. ✅ **Complete architecture**: ViewModels, Repositories, Navigation
7. ✅ **Game logic**: Bulls/cows calculator, AI opponent, validation
8. ✅ **Animations**: Spring-based, haptic feedback, sound effects
9. ✅ **Testing strategy**: Unit and UI tests
10. ✅ **Build instructions**: Dependencies, Firebase setup, project structure

**Estimated Timeline**:
- Week 1-2: Core UI (Home, Difficulty, Game Board) + VS Computer mode
- Week 3-4: Multiplayer (Firebase setup, room creation, real-time sync)
- Week 5-6: Polish (animations, sounds, victory screen, settings)

**Key Files to Generate First**:
1. `GameEngine.kt` - Core game logic
2. `GameBoardScreen.kt` - Main gameplay UI
3. `MultiplayerRepository.kt` - Firebase integration
4. `Navigation.kt` - Screen routing

This spec is ready to hand off to Claude Code or any Android developer. Every screen, component, and system is detailed with code examples and clear requirements.
