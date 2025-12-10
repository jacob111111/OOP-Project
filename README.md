# Chess Game - OOP Project

A Java-based chess game implementation demonstrating core OOP principles including inheritance, polymorphism, encapsulation, and abstraction through a fully functional chess game with both GUI and console interfaces.

## Team Members

- Jordan Atchison
- Jacob Atchison

## Quick Start

### Requirements

- Java JDK 8 or higher
- Internet connection (for AI opponent)

### Compile and Run

```cmd
# Navigate to source directory
cd src

# Compile
javac Main.java

# Run
java Main
```

Choose option 1 for GUI mode or option 2 for Console mode.

## How to Play

### Initial Setup

1. Run the application and choose your interface:
   - **Option 1**: GUI (Combined Board & Menu) - Recommended
   - **Option 2**: Console (Text-based)

### GUI Mode

1. **Starting a Game**:

   - Click "2-Player" button to start a local game
   - Click "vs Computer" to play against the AI

2. **Making Moves**:

   - **Drag and Drop**: Click and drag a piece to its destination
   - **Click to Move**: Click a piece to select it (highlighted border), then click the destination square
   - Invalid moves will show a warning popup

3. **Game Controls**:

   - **New Game**: Reset the board and start a new game
   - **Save Game**: Save the current game state to a file
   - **Load Game**: Load a previously saved game

4. **Customization**:

   - **Board Theme**: Switch between Classic and Modern board colors
   - **Piece Theme**: Switch between Classic and Modern piece graphics

5. **Winning**:
   - The game automatically detects checkmate
   - A popup will announce the winner

### Console Mode

1. **Game Setup**:

   - Choose 1-Player (vs AI) or 2-Player
   - Select Player 1's color (White or Black)

2. **Making Moves**:

   - Enter moves using algebraic notation
   - Format: `[from_square]` then `[to_square]`
   - Example: Type `e2` (press Enter), then type `e4` (press Enter)

3. **Board Display**:

   - Board shows current position
   - Pieces displayed as: `wK` (white king), `bP` (black pawn), etc.
   - Empty squares shown as `##`
   - Board orientation flips based on whose turn it is

### Playing Against AI

1. Start a new game and select "1-Player (AI)"
2. Choose difficulty: Easy (depth 5), Medium (depth 10), or Hard (depth 15)
3. Select your color
4. The AI uses Stockfish via lichess.org cloud API - no setup required!

**Note:** AI requires an internet connection to work.

## Project Structure

```
src/
├── board/              # Chess board representation and state management
│   └── Board.java      # Board logic, move validation, piece tracking, checkmate detection
├── game/               # Game flow and different game modes
│   ├── Game.java       # Abstract base class (Template Method pattern)
│   ├── Console.java    # Console-based gameplay implementation
│   └── GUI.java        # GUI game controller and event handler
├── gui/                # Swing-based graphical interface
│   ├── ChessFrame.java # Main application window
│   ├── board/          # Board rendering and interaction
│   │   ├── BoardPanel.java       # Core board rendering and piece display
│   │   ├── MainBoardPanel.java   # Main board container
│   │   ├── LabelPanel.java       # Board coordinate labels
│   │   └── MoveState.java        # Move state tracking for GUI
│   ├── menu/           # Menu panels and controls
│   │   ├── SideMenuPanel.java        # Game controls and settings
│   │   ├── GameControlPanel.java     # Game control buttons
│   │   ├── GameInfoPanel.java        # Game information display
│   │   ├── GameModeDialog.java       # Game mode selection dialog
│   │   ├── AIDifficultyDialog.java   # AI difficulty selection
│   │   ├── OnlineGameDialog.java     # Online game setup
│   │   ├── SaveLoadManager.java      # Save/load functionality
│   │   ├── MessageBoardPanel.java    # Message display
│   │   └── ThemeSettingsPanel.java   # Theme customization
│   ├── utils/          # GUI utilities and styling
│   │   ├── UIPalette.java        # Color schemes and themes
│   │   └── UIStyle.java          # Component styling utilities
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
│   ├── AI.java         # AI player with Stockfish integration
│   ├── Server.java     # Network game server
│   └── Client.java     # Network game client
└── utils/              # Utility classes and algorithms
    ├── AttackMap.java          # Efficient attack square tracking
    ├── CheckmateDetector.java  # Checkmate detection algorithm
    ├── MoveValidator.java      # Move validation logic
    ├── Position.java           # Board position representation
    ├── Color.java              # Player color enum
    ├── NetworkMessage.java     # Network message structure
    └── NetworkMessageType.java # Network message types
```

## Architecture Overview

The project follows a clear separation of concerns with the **Board** class serving as the central authority for game logic:

- **Board**: Handles all move validation using AttackMap, manages piece positions, tracks captures, and coordinates with CheckmateDetector for game-ending conditions
- **Game**: Manages turn flow and delegates move requests to Board for validation
- **Player**: Manages piece collections and provides piece lookup functionality
- **Piece**: Individual pieces handle their own position updates after Board validates moves
- **AttackMap**: Provides O(1) lookup for which squares are under attack, accounting for blocked paths
- **CheckmateDetector**: Uses AttackMap to efficiently determine checkmate conditions
