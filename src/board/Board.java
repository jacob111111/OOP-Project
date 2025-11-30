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
 * Represents the chess board and manages piece positions and game state.
 * 
 * The Board class is responsible for:
 * - Managing piece positions using an efficient position index for O(1) lookups
 * - Tracking captured pieces for both players
 * - Handling piece movement and capture mechanics
 * - Displaying the board state to players
 * - Managing player instances (human players and AI)
 * 
 * The board uses a coordinate system where positions are represented as
 * (x,y) coordinates with (0,0) at a1 and (7,7) at h8 in standard chess
 * notation.
 * 
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
     * Constructs a new chess board with players based on game mode.
     * 
     * Creates appropriate player instances based on whether this is a
     * Player vs Player game or Player vs AI game. For PvP mode, both
     * players are human. For PvE mode, one player is human and the other is AI.
     * 
     * @param isPvP   true for Player vs Player mode, false for Player vs AI
     * @param P1Color the color that Player 1 will control (WHITE, BLACK, or RANDOM)
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
     * Updates a piece's position in both the piece object and the position index.
     * 
     * This method maintains consistency between the piece's internal position
     * and the board's position tracking system. Note that the attack map is
     * updated during move validation, so no additional update is needed here.
     * 
     * @param piece  the piece being moved
     * @param oldPos the piece's previous position
     * @param newPos the piece's new position
     */
    public void updatePiecePosition(Piece piece, Position oldPos, Position newPos) {
        positionIndex.remove(oldPos);
        positionIndex.put(newPos, piece);
        piece.setPosition(newPos);
    }

    /**
     * Handles piece capture mechanics while maintaining position index consistency.
     * 
     * This method is called once a valid capture has been confirmed. It removes
     * the captured piece from the appropriate player's piece collection, adds it
     * to the capturing player's capture list, and removes from position index.
     * 
     * @param capturingPiece the piece performing the capture
     * @param capturePos     the position where the capture occurs
     * @param capturedPiece  the piece being captured
     * @return true if a King was captured, false otherwise
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
     * Attempts to move a piece to the specified position.
     * 
     * Validates the move using MoveValidator which checks:
     * 1. Can the piece reach the target square?
     * 2. Does the move leave the king in check?
     * 
     * Note: Ownership validation should be done by the caller (GUI/Game layer).
     * 
     * @param possibleMove the target position for the move
     * @param pieceToMove  the piece that should be moved
     * @return true if the move was successful, false if invalid
     */
    public boolean attemptMove(Position possibleMove, Piece pieceToMove) {
        Position oldPosition = pieceToMove.getPosition();

        // Validate reachability and king safety using MoveValidator
        if (!moveValidator.isDestinationReachable(pieceToMove, possibleMove)) {
            return false;
        }

        if (!moveValidator.isMoveKingSafe(pieceToMove, oldPosition, possibleMove)) {
            return false;
        }

        // Move is valid and safe, execute it
        pieceToMove.move(possibleMove);

        // Update pawn's hasMoved flag after first move
        if (pieceToMove instanceof Pawn) {
            ((Pawn) pieceToMove).setHasMoved(true);
        }

        // Update king's hasMoved flag after first move
        if (pieceToMove instanceof King) {
            ((King) pieceToMove).setHasMoved(true);
        }

        // Update rook's hasMoved flag after first move
        if (pieceToMove instanceof Rook) {
            ((Rook) pieceToMove).setHasMoved(true);
        }

        return true;
    }

    /**
     * Checks if a move is a castling move.
     * Castling is when the king moves two squares horizontally.
     * 
     * @param piece        the piece being moved
     * @param fromPosition the starting position
     * @param toPosition   the destination position
     * @return true if this is a castling move, false otherwise
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
     * Executes a castling move by moving both king and rook.
     * 
     * @param king     the king piece
     * @param kingFrom king's starting position
     * @param kingTo   king's destination position
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
            updatePiecePosition(rook, rookFrom, rookTo);
            rook.setHasMoved(true);

            System.out.println(
                    "Castling executed: King " + kingFrom + " -> " + kingTo + ", Rook " + rookFrom + " -> " + rookTo);
        }
    }

    /**
     * Checks if a move results in pawn promotion.
     * Pawn promotion occurs when a pawn reaches the opposite end of the board.
     * 
     * @param piece      the piece being moved
     * @param toPosition the destination position
     * @return true if this move should trigger pawn promotion, false otherwise
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
     * @param pawn          the pawn to promote
     * @param position      the position where the pawn is located
     * @param promotionType the type of piece to promote to (Q, R, B, N)
     * @return the newly created piece
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
     * Converts chess notation (like "e4") to a Position object
     * Chess board: a-h columns (0-7), 1-8 rows (0-7)
     * 
     * @param notation Chess notation string (e.g., "e4")
     * @return Position object or null if invalid notation
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
}