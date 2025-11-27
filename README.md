# Chess Game - OOP Project

A Java-based chess game implementation developed as part of CS3354 Object-Oriented Programming course. This project demonstrates core OOP principles including inheritance, polymorphism, encapsulation, and abstraction through a fully functional chess game with both GUI and console interfaces.

## Team Members

- Jordan Atchison
- Jacob Atchison

## Features

### Currently Implemented ✅

#### Core Game Mechanics

- Complete chess piece movement rules and validation for all piece types
- Piece capture mechanics with visual feedback
- Turn-based gameplay for two players
- **Checkmate detection system** using optimized attack maps
- King capture win condition
- Support for standard chess notation (e.g., e2 to e4)

#### User Interfaces

- **GUI Mode** with Swing components featuring:
  - Drag-and-drop piece movement with visual feedback
  - Click-to-move piece movement system
  - Real-time board updates and piece animations
  - Side menu with game controls and settings
  - Multiple visual themes (Classic/Modern board themes)
  - Multiple piece themes (Classic/Modern piece graphics)
  - Game state management (New Game, Save Game, Load Game)
  - Win condition popups and game-over handling
- **Console Mode** with text-based interface:
  - ASCII board representation
  - Standard algebraic notation input
  - Board orientation based on current player
  - Turn-by-turn gameplay

#### Advanced Features

- **AttackMap System**: Efficient O(1) square attack lookup using pre-computed hash maps
- **CheckmateDetector**: Sophisticated checkmate detection with king escape analysis
- **Position Index**: O(1) piece position lookups using hash map
- **Serializable game state**: Support for save/load functionality
- **Flexible player system**: Player vs Player with AI framework in place

### Planned Features 📋

- Full AI opponent with move evaluation algorithms
- Network-based multiplayer (LAN)
- En passant capture
- Castling moves
- Pawn promotion
- Stalemate detection
- Move history and undo functionality
- Chess clock/timer

## Project Structure

```
src/
├── board/              # Chess board representation and state management
│   └── Board.java      # Board logic, piece tracking, position indexing
├── game/               # Game flow and different game modes
│   ├── Game.java       # Abstract base class (Template Method pattern)
│   ├── Console.java    # Console-based gameplay implementation
│   └── GUI.java        # GUI game controller and event handler
├── gui/                # Swing-based graphical interface
│   ├── chessFrame.java # Main application window
│   ├── board/          # Board rendering and interaction
│   │   ├── BoardPanel.java       # Core board rendering and piece display
│   │   ├── MainBoardPanel.java   # Main board container
│   │   ├── LabelPanel.java       # Board coordinate labels
│   │   └── MoveState.java        # Move state tracking for GUI
│   ├── menu/           # Menu panels and controls
│   │   ├── sideMenuPanel.java    # Game controls and settings
│   │   ├── mainMenuPanel.java    # Main menu interface
│   │   └── settingsPanel.java    # Settings configuration
│   ├── utils/          # GUI utilities and styling
│   │   ├── UIPalette.java        # Color schemes and themes
│   │   ├── UIStyle.java          # Component styling utilities
│   │   └── gameState.java        # GUI game state management
│   └── images/         # Piece graphics
│       ├── classic/    # Classic piece style
│       └── modern/     # Modern piece style
├── piece/              # Chess piece implementations
│   ├── Piece.java      # Abstract base class for all pieces
│   ├── LinearPiece.java # Base for Queen, Rook, Bishop
│   ├── King.java       # King piece with castling support
│   ├── Queen.java      # Queen piece
│   ├── Rook.java       # Rook piece
│   ├── Bishop.java     # Bishop piece
│   ├── Knight.java     # Knight piece
│   └── Pawn.java       # Pawn piece with en passant support
├── player/             # Player implementations
│   ├── Player.java     # Human player with piece management
│   └── AI.java         # AI player (framework in place)
└── utils/              # Utility classes and algorithms
    ├── AttackMap.java          # Efficient attack square tracking
    ├── CheckmateDetector.java  # Checkmate detection algorithm
    ├── Position.java           # Board position representation
    ├── Color.java              # Player color enum
    └── GameType.java           # Game mode enum
```

## How to Run

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Command line terminal

### Compilation and Execution

#### Windows (CMD)

```cmd
# Navigate to project root directory
cd "c:\Users\jreed\Coding Classes\OOp\OOP-Project"

# Compile all Java files
javac -d . src\Main.java src\board\*.java src\game\*.java src\piece\*.java src\player\*.java src\utils\*.java src\gui\*.java src\gui\board\*.java src\gui\menu\*.java src\gui\utils\*.java

# Run the application
java Main
```

#### Linux/Mac (Bash)

```bash
# Navigate to project root directory
cd /path/to/OOP-Project

# Compile all Java files
javac -d . src/Main.java src/board/*.java src/game/*.java src/piece/*.java src/player/*.java src/utils/*.java src/gui/*.java src/gui/board/*.java src/gui/menu/*.java src/gui/utils/*.java

# Run the application
java Main
```

## How to Play

### Initial Setup

1. Run the application and choose your interface:
   - **Option 1**: GUI (Combined Board & Menu) - Recommended
   - **Option 2**: Console (Text-based)

### GUI Mode

1. **Starting a Game**:

   - Click "2-Player" button in the side menu to start a new game
   - ("1-Player (AI)" is currently in development)

2. **Making Moves**:

   - **Drag and Drop**: Click and drag a piece to its destination
   - **Click to Move**: Click a piece to select it (highlighted border), then click the destination square
   - Invalid moves will show a warning popup

3. **Game Controls**:

   - **New Game**: Clear the current game and reset the board
   - **Save Game**: Save the current game state to a file
   - **Load Game**: Load a previously saved game

4. **Customization**:

   - **Board Theme**: Switch between Classic and Modern board colors
   - **Piece Theme**: Switch between Classic and Modern piece graphics

5. **Winning**:
   - Capture the opponent's King to win
   - Checkmate detection will identify when the game ends
   - A popup will announce the winner

### Console Mode

1. **Game Setup**:

   - Choose 1-Player (vs AI - in development) or 2-Player
   - Select Player 1's color (White or Black)

2. **Making Moves**:

   - Enter moves using algebraic notation
   - Format: `[from_square]` then `[to_square]`
   - Example: Type `e2` (press Enter), then type `e4` (press Enter)
   - Moves from e2 to e4 (pawn advance)

3. **Board Display**:

   - Board shows current position
   - Pieces displayed as: `wK` (white king), `bP` (black pawn), etc.
   - Empty squares shown as `##`
   - Board orientation flips based on whose turn it is

4. **Game Validation**:
   - Invalid moves are rejected with error messages
   - Game automatically detects checkmate
   - Turn alternation enforced

### Chess Notation Reference

- Files (columns): a-h (left to right)
- Ranks (rows): 1-8 (bottom to top for white)
- Example positions: a1, e4, h8
- Starting position: White pieces on ranks 1-2, Black pieces on ranks 7-8
