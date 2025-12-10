package board;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import piece.*;
import player.*;
import utils.AttackMap;
import utils.CheckmateDetector;
import utils.MoveValidator;
import utils.Color;
import utils.Position;

/**
 * Chess board that manages piece positions and game state.
 * Uses efficient position indexing for O(1) lookups and tracks captured pieces.
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The white player instance */
    protected Player white, black;

    /** Dictionary tracking pieces captured by white player */
    private Dictionary<Piece, Integer> whiteHasCaptured = new Hashtable<>();

    /** Dictionary tracking pieces captured by black player */
    private Dictionary<Piece, Integer> blackHasCaptured = new Hashtable<>();

    /** Hash map for O(1) piece position lookups */
    private Map<Position, Piece> positionIndex = new HashMap<>();

    /** Cached attack map for efficient move validation and checkmate detection */
    private transient AttackMap attackMap;

    /** Checkmate detector using the cached attack map */
    private transient CheckmateDetector checkmateDetector;

    /** Move validator for centralized move validation logic */
    private transient MoveValidator moveValidator;

    /**
     * Creates a chess board with players based on game mode.
     * 
     * @param isPvP   true for Player vs Player, false for Player vs AI
     * @param P1Color player 1's color (WHITE, BLACK, or RANDOM)
     */
    public Board(boolean isPvP, Color P1Color) {
        if (isPvP) {
            this.white = new Player(Color.WHITE);
            this.black = new Player(Color.BLACK);
        } else {
            Random rand = new Random();
            if (P1Color == Color.RANDOM) { // coin flip decides users (p1's) color
                if (rand.nextBoolean()) {
                    P1Color = Color.WHITE;
                } else {
                    P1Color = Color.BLACK;
                }
            }
            if (P1Color == Color.WHITE) {
                // user chooses white
                this.white = new Player(Color.WHITE);
                this.black = new AI(Color.BLACK);
            } else {
                // user chooses black
                this.white = new AI(Color.WHITE);
                this.black = new Player(Color.BLACK);
            }
        }

        // Initialize position index for O(1) lookups
        // Add all white pieces to index
        for (Piece piece : white.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }
        // Add all black pieces to index
        for (Piece piece : black.getCurrentPieces()) {
            positionIndex.put(piece.getPosition(), piece);
        }

        // Initialize attack map, move validator, and checkmate detector
        this.attackMap = new AttackMap(this);
        this.moveValidator = new MoveValidator(this, attackMap);
        this.checkmateDetector = new CheckmateDetector(this, attackMap);
        // Set MoveValidator on CheckmateDetector (avoid circular dependency)
        this.checkmateDetector.setMoveValidator(moveValidator);
    }

    /**
     * Gets the dictionary of pieces captured by the specified color.
     * 
     * @param colorOfPiece the color whose captures to retrieve (WHITE or BLACK)
     * @return Dictionary containing captured pieces and their IDs
     */
    public Dictionary<Piece, Integer> getCaptures(Color colorOfPiece) {
        return colorOfPiece == Color.WHITE ? whiteHasCaptured : blackHasCaptured;
    }

    /**
     * Gets the player instance for the specified color.
     * 
     * @param colorOfPiece the color of the player to retrieve (WHITE or BLACK)
     * @return The Player or AI instance for that color
     */
    public Player getPlayer(Color colorOfPiece) {
        return colorOfPiece == Color.WHITE ? white : black;
    }

    /**
     * Gets the checkmate detector for this board.
     * 
     * @return The CheckmateDetector instance
     */
    public CheckmateDetector getCheckmateDetector() {
        return checkmateDetector;
    }

    /**
     * Gets the MoveValidator instance for move validation.
     * 
     * @return The MoveValidator instance
     */
    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

    /**
     * Gets the AttackMap instance for attack tracking.
     * 
     * @return The AttackMap instance
     */
    public AttackMap getAttackMap() {
        return attackMap;
    }

    /**
     * Gets the piece at the specified position, if any.
     * 
     * Uses the position index for O(1) lookup performance.
     * 
     * @param pos the position to check
     * @return The piece at that position, or null if empty
     */
    public Piece getPieceAt(Position pos) {
        return positionIndex.get(pos);
    }

    /**
     * Gets the position index map for direct access.
     * Needed for move simulation and network synchronization.
     * 
     * @return The position index map
     */
    public Map<Position, Piece> getPositionIndex() {
        return positionIndex;
    }

    /**
     * Handles piece capture mechanics and position index updates.
     * Removes captured piece from player's collection and adds to capture list.
     * 
     * @param capturingPiece the piece performing the capture
     * @param capturePos     position where capture occurs
     * @param capturedPiece  piece being captured
     * @return true if King was captured
     */
    public boolean capturePiece(Piece capturingPiece, Position capturePos, Piece capturedPiece) {
        boolean kingCaptured = false;

        if (capturedPiece != null) {
            // Check if the captured piece is a King
            if (capturedPiece instanceof King) {
                kingCaptured = true;
            }

            // Remove from appropriate player's piece list
            if (capturedPiece.getColor() == Color.WHITE) {
                white.getCurrentPieces().remove(capturedPiece);
                blackHasCaptured.put(capturedPiece, capturedPiece.hashCode()); // Using hashCode as ID
            } else {
                black.getCurrentPieces().remove(capturedPiece);
                whiteHasCaptured.put(capturedPiece, capturedPiece.hashCode()); // Using hashCode as ID
            }

            // Remove captured piece from position index
            positionIndex.remove(capturePos);
        }

        return kingCaptured;
    }

    /**
     * Validates and executes a move if legal.
     * Checks reachability and king safety, then handles captures and updates board
     * state.
     * 
     * @param fromPosition starting position
     * @param toPosition   target position
     * @param pieceToMove  piece to move
     * @return true if move was valid and executed
     */
    public boolean attemptMove(Position fromPosition, Position toPosition, Piece pieceToMove) {
        // Validate reachability and king safety using MoveValidator
        if (!moveValidator.isDestinationReachable(pieceToMove, toPosition)) {
            return false;
        }

        if (!moveValidator.isMoveKingSafe(pieceToMove, fromPosition, toPosition)) {
            return false;
        }

        // Move is valid and safe - handle capture BEFORE moving
        Piece capturedPiece = getPieceAt(toPosition);
        if (capturedPiece != null && capturedPiece.getColor() != pieceToMove.getColor()) {
            // Remove captured piece from player's pieces and add to captures
            if (capturedPiece.getColor() == Color.WHITE) {
                white.getCurrentPieces().remove(capturedPiece);
                blackHasCaptured.put(capturedPiece, capturedPiece.hashCode());
            } else {
                black.getCurrentPieces().remove(capturedPiece);
                whiteHasCaptured.put(capturedPiece, capturedPiece.hashCode());
            }
            // Position will be cleared when we update the moving piece's position
        }

        // Update piece flags for special moves
        if (pieceToMove instanceof Pawn) {
            ((Pawn) pieceToMove).setHasMoved(true);
        }

        if (pieceToMove instanceof King) {
            ((King) pieceToMove).setHasMoved(true);
        }

        if (pieceToMove instanceof Rook) {
            ((Rook) pieceToMove).setHasMoved(true);
        }

        // Update piece position and board index (this overwrites the captured piece in
        // index)
        positionIndex.remove(fromPosition);
        positionIndex.put(toPosition, pieceToMove);
        pieceToMove.setPosition(toPosition);

        // Update attack map to reflect the new board state
        attackMap.updateAfterMove(pieceToMove, fromPosition, toPosition);

        return true;
    }

    /**
     * Checks if a move is castling (king moves two squares horizontally).
     * 
     * @param piece        piece being moved
     * @param fromPosition starting position
     * @param toPosition   destination position
     * @return true if this is castling
     */
    public boolean isCastlingMove(Piece piece, Position fromPosition, Position toPosition) {
        if (!(piece instanceof King)) {
            return false;
        }
        int dx = Math.abs(toPosition.getX() - fromPosition.getX());
        int dy = Math.abs(toPosition.getY() - fromPosition.getY());
        // Castling: king moves 2 squares horizontally
        return dx == 2 && dy == 0;
    }

    /**
     * Executes castling by moving both king and rook.
     * 
     * @param king     king piece
     * @param kingFrom king's starting position
     * @param kingTo   king's destination
     */
    public void executeCastling(King king, Position kingFrom, Position kingTo) {
        // Determine if king-side or queen-side castling
        boolean kingside = kingTo.getX() > kingFrom.getX();

        // Calculate rook positions
        int rookFromX = kingside ? 7 : 0;
        int rookToX = kingside ? kingTo.getX() - 1 : kingTo.getX() + 1;
        int y = kingFrom.getY();

        Position rookFrom = new Position(rookFromX, y);
        Position rookTo = new Position(rookToX, y);

        // Get the rook
        Piece rookPiece = getPieceAt(rookFrom);
        if (rookPiece instanceof Rook) {
            Rook rook = (Rook) rookPiece;

            // Move the rook
            positionIndex.remove(rookFrom);
            positionIndex.put(rookTo, rook);
            rook.setPosition(rookTo);
            rook.setHasMoved(true);

            System.out.println(
                    "Castling executed: King " + kingFrom + " -> " + kingTo + ", Rook " + rookFrom + " -> " + rookTo);
        }
    }

    /**
     * Checks if a move triggers pawn promotion (pawn reaches opposite end).
     * 
     * @param piece      piece being moved
     * @param toPosition destination
     * @return true if pawn promotion should occur
     */
    public boolean isPawnPromotion(Piece piece, Position toPosition) {
        if (!(piece instanceof Pawn)) {
            return false;
        }
        // White pawns promote on rank 8 (y=7), black pawns on rank 1 (y=0)
        return (piece.getColor() == Color.WHITE && toPosition.getY() == 7) ||
                (piece.getColor() == Color.BLACK && toPosition.getY() == 0);
    }

    /**
     * Promotes a pawn to the specified piece type.
     * 
     * @param pawn          pawn to promote
     * @param position      pawn's position
     * @param promotionType piece type (Q, R, B, N)
     * @return newly created piece
     */
    public Piece promotePawn(Pawn pawn, Position position, String promotionType) {
        Piece newPiece = null;
        Color color = pawn.getColor();

        switch (promotionType.toUpperCase()) {
            case "Q":
                newPiece = new Queen(color, position);
                break;
            case "R":
                newPiece = new Rook(color, position);
                ((Rook) newPiece).setHasMoved(true); // Promoted rook has "moved"
                break;
            case "B":
                newPiece = new Bishop(color, position);
                break;
            case "N":
                newPiece = new Knight(color, position);
                break;
            default:
                // Default to queen if invalid type
                newPiece = new Queen(color, position);
        }

        // Remove pawn from player's pieces
        Player player = getPlayer(color);
        player.getCurrentPieces().remove(pawn);

        // Add new piece to player's pieces
        player.getCurrentPieces().add(newPiece);

        // Update position index
        positionIndex.put(position, newPiece);

        System.out.println("Pawn promoted to " + newPiece.getClass().getSimpleName() + " at " + position);

        return newPiece;
    }

    /**
     * Converts chess notation (e.g., "e4") to a Position object.
     * 
     * @param notation chess notation string
     * @return Position object or null if invalid
     */
    public Position chessNotationToPosition(String notation) {
        if (notation == null || notation.length() != 2) {
            return null;
        }

        char file = notation.toLowerCase().charAt(0); // column (a-h)
        char rank = notation.charAt(1); // row (1-8)

        // Validate input
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            return null;
        }

        // Convert to 0-based coordinates
        int x = file - 'a'; // a=0, b=1, ..., h=7
        int y = rank - '1'; // 1=0, 2=1, ..., 8=7

        return new Position(x, y);
    }

    /**
     * Converts a Position object to chess notation (e.g., "e4").
     * 
     * @param position Position object to convert
     * @return chess notation string or null if invalid
     */
    public String positionToChessNotation(Position position) {
        if (position == null || position.getX() < 0 || position.getX() > 7
                || position.getY() < 0 || position.getY() > 7) {
            return null;
        }

        // Convert 0-based coordinates to chess notation
        char file = (char) ('a' + position.getX()); // 0=a, 1=b, ..., 7=h
        char rank = (char) ('1' + position.getY()); // 0=1, 1=2, ..., 7=8

        return "" + file + rank;
    }

    /**
     * Checks if this is a Player vs Player game or Player vs AI.
     * 
     * @return true if both players are human, false if playing against AI
     */
    public boolean isPvP() {
        return !(white instanceof AI) && !(black instanceof AI);
    }

    /**
     * Converts board state to FEN (Forsyth-Edwards Notation).
     * Returns simplified FEN with piece placement only.
     * 
     * @param activeColor whose turn it is
     * @return FEN string representing board state
     */
    public String toFEN(Color activeColor) {
        StringBuilder fen = new StringBuilder();

        // Build piece placement (starting from rank 8 down to rank 1)
        for (int rank = 7; rank >= 0; rank--) {
            int emptyCount = 0;

            for (int file = 0; file < 8; file++) {
                Position pos = new Position(file, rank);
                Piece piece = getPieceAt(pos);

                if (piece == null) {
                    emptyCount++;
                } else {
                    // Add empty squares count if any
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }

                    // Add piece symbol (uppercase for white, lowercase for black)
                    char pieceChar = getPieceFENChar(piece);
                    fen.append(pieceChar);
                }
            }

            // Add remaining empty squares count
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }

            // Add rank separator (except after last rank)
            if (rank > 0) {
                fen.append('/');
            }
        }

        // Add active color (w or b)
        fen.append(' ');
        fen.append(activeColor == Color.WHITE ? 'w' : 'b');

        // Simplified FEN - no castling rights, en passant, or move counters
        // Full implementation would track these in game state
        fen.append(" - - 0 1");

        return fen.toString();
    }

    /**
     * Gets the FEN character for a piece.
     * 
     * @param piece piece to convert
     * @return FEN character (K/Q/R/B/N/P for white, lowercase for black)
     */
    private char getPieceFENChar(Piece piece) {
        char baseChar;

        if (piece instanceof King) {
            baseChar = 'K';
        } else if (piece instanceof Queen) {
            baseChar = 'Q';
        } else if (piece instanceof Rook) {
            baseChar = 'R';
        } else if (piece instanceof Bishop) {
            baseChar = 'B';
        } else if (piece instanceof Knight) {
            baseChar = 'N';
        } else if (piece instanceof Pawn) {
            baseChar = 'P';
        } else {
            baseChar = '?';
        }

        // Lowercase for black pieces
        if (piece.getColor() == Color.BLACK) {
            baseChar = Character.toLowerCase(baseChar);
        }

        return baseChar;
    }

    /**
     * Custom deserialization method to reinitialize transient fields.
     * Called automatically during deserialization to restore non-serializable
     * helper objects.
     * 
     * this is english ^
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Reinitialize transient helper objects after deserialization
        this.attackMap = new AttackMap(this);
        this.moveValidator = new MoveValidator(this, attackMap);
        this.checkmateDetector = new CheckmateDetector(this, attackMap);
        this.checkmateDetector.setMoveValidator(moveValidator);
    }
}