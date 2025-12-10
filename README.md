# GUI Chess Game Project

A Java-based chess game implementation demonstrating core OOP principles including inheritance, polymorphism, encapsulation, and abstraction through a fully functional chess game with both GUI and console interfaces.

## Team Information
- **Team Name**: Lukewarm Bread
- Jordan Atchison
- Jacob Atchison
- **Semester**: Fall 2024
- **Course Number and Section**: CS3354 - Section XYZ

---

## Project Overview
This project is a GUI-based Chess game developed for our Object-Oriented Programming course. It combines backend logic from our console-based Phase 1 implementation with a graphical interface built in Phase 2, creating a complete chess game experience with full gameplay functionality.

---

## Preview
### GUI Preview
![Chess Game GUI Preview](path/to/your/gui_preview_image.png)

### Class Diagram
![Class Diagram](path/to/your/class_diagram_image.png)

---

## How to Run the Project
Follow these instructions to compile and run the project:

1. **Clone the Repository**:  
   ```bash
   git clone [repository link]
   cd [repository folder]
   ```

2. **Compile the Code**:  
   ```bash
   javac -d bin src/*.java
   ```

3. **Run the Game**:  
   ```bash
   java -cp bin ChessGame
   ```

Choose option 1 for GUI mode or option 2 for Console mode.
   
---

## Features Checklist
- [x] GUI with an 8x8 Chessboard
- [x] Piece Movement with Mouse Interaction
- [x] Move Validation Based on Chess Rules
- [x] Capture Mechanism
- [x] Check and Checkmate Detection
- [x] Turn-Based Play for Two Players
- [x] Game End Notification on Checkmate
- [x] Single Player/ AI Game Mode
- [x] Themes for Chess Board and Pieces
- [x] LAN/ Online Game Mode 

---

### Playing Against AI ###

1. Start a new game and select "1-Player (AI)"
2. Choose difficulty: Easy (depth 5), Medium (depth 10), or Hard (depth 15)
3. Select your color
4. The AI uses Stockfish via lichess.org cloud API - no setup required!

**Note:** AI requires an internet connection to work.

---

## Architecture Overview

The project follows a clear separation of concerns with the **Board** class serving as the central authority for game logic:

- **Board**: Handles all move validation using AttackMap, manages piece positions, tracks captures, and coordinates with CheckmateDetector for game-ending conditions
- **Game**: Manages turn flow and delegates move requests to the Board for validation
- **Player**: Manages piece collections and provides piece lookup functionality
- **Piece**: Individual pieces handle their own position updates after the Board validates moves
- **AttackMap**: Provides O(1) lookup for which squares are under attack, accounting for blocked paths
- **CheckmateDetector**: Uses AttackMap to determine checkmate conditions efficiently


## Full Project Structure ##

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

---

## Additional Information
For details on Phases 1 and 2, please refer to the [branches/folders] designated for those implementations. Detailed documentation for those phases is not included in this README.

---

## Acknowledgements
Special thanks to Professor Xiaomin Li and our team members for their support and collaboration.
