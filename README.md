# Chess Game - OOP Project

A Java-based chess game implementation developed as part of CS3354 Object-Oriented Programming course. This project demonstrates core OOP principles including inheritance, polymorphism, encapsulation, and abstraction through a fully functional ~~(getting there)~~ chess game.

## Team Members

- Jordan Atchison
- Jacob Atchison

## Features

### Currently Implemented

- Console-based chess gameplay
- Complete chess piece movement rules and validation
- Piece capture mechanics
- Turn-based gameplay for two players
- Chess board display with proper piece representation
- Support for standard chess notation (e.g., e2 to e4)

### Planned Features

- Player vs AI gameplay
- Network-based multiplayer (LAN)
- Check and checkmate detection
- Game state persistence

## Project Structure

```
src/
├── board/          # Chess board representation and management
├── game/           # Game flow and different game modes
├── piece/          # Chess piece implementations
├── player/         # Player and AI implementations
└── utils/          # Utility classes and enums
```

## How to Run

1. Make sure you have Java installed (Java 8 or higher)
2. Navigate to the project directory
3. Compile the project:
   ```
   javac -cp . Main.java src/**/*.java
   ```
4. Run the game:
   ```
   java Main
   ```

## How to Play

1. Choose your game mode (currently only console mode is implemented)
2. Select your color (White or Black)
3. Enter moves using standard chess notation:
   - Format: `[from_square] [to_square]`
   - Example: `e2 e4` (moves pawn from e2 to e4)
4. The game will validate moves and handle piece capture automatically
