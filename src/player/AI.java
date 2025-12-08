package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import board.Board;
import piece.*;
import utils.Color;
import utils.MoveValidator;
import utils.Position;

/**
 * Represents an AI (computer) player in the chess game.
 * 
 * Simple AI with three difficulty levels:
 * - Easy: Random moves
 * - Medium: Prefers captures and checks
 * - Hard: Evaluates material balance
 * 
 * @see Player
 */
public class AI extends Player {
    private int difficulty; // 1=Easy, 2=Medium, 3=Hard
    private Random random;

    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;

    public AI(Color color) {
        this(color, 2);
    }

    public AI(Color color, int difficulty) {
        super(color);
        this.difficulty = Math.max(1, Math.min(3, difficulty));
        this.random = new Random();
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(3, difficulty));
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Move getBestMove(Board board) {
        List<Move> allMoves = generateAllLegalMoves(board);
        if (allMoves.isEmpty())
            return null;

        Move best;
        switch (difficulty) {
            case 1:
                best = allMoves.get(random.nextInt(allMoves.size()));
                break;
            case 2:
                best = selectMediumMove(board, allMoves);
                break;
            case 3:
                best = selectHardMove(board, allMoves);
                break;
            default:
                best = allMoves.get(0);
        }
        System.out.println("AI: " + best);
        return best;
    }

    private Move selectMediumMove(Board board, List<Move> moves) {
        List<Move> captures = new ArrayList<>();

        for (Move m : moves) {
            Piece target = board.getPieceAt(m.to);
            if (target != null && target.getColor() != color)
                captures.add(m);
        }

        // Prioritize captures
        if (!captures.isEmpty())
            return captures.get(random.nextInt(captures.size()));

        // Otherwise pick random move
        return moves.get(random.nextInt(moves.size()));
    }

    private Move selectHardMove(Board board, List<Move> moves) {
        Move best = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move m : moves) {
            // Evaluate static position after this move
            int score = evaluateMoveScore(board, m);

            if (score > bestScore) {
                bestScore = score;
                best = m;
            }
        }
        return best != null ? best : moves.get(0);
    }

    /**
     * Evaluates the value of a specific move.
     * Considers material gain from captures.
     */
    private int evaluateMoveScore(Board board, Move m) {
        int score = 0;

        // Check if this move captures an opponent piece
        Piece targetPiece = board.getPieceAt(m.to);
        if (targetPiece != null && targetPiece.getColor() != color) {
            // Add value for capturing opponent's piece
            score += getPieceValue(targetPiece);
        }

        return score;
    }

    private int getPieceValue(Piece p) {
        if (p instanceof Pawn)
            return PAWN_VALUE;
        if (p instanceof Knight)
            return KNIGHT_VALUE;
        if (p instanceof Bishop)
            return BISHOP_VALUE;
        if (p instanceof Rook)
            return ROOK_VALUE;
        if (p instanceof Queen)
            return QUEEN_VALUE;
        if (p instanceof King)
            return KING_VALUE;
        return 0;
    }

    private List<Move> generateAllLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        MoveValidator validator = board.getMoveValidator();

        for (Piece piece : getCurrentPieces()) {
            Set<Position> possibleMoves = piece.getLegalMoves(board);
            Position from = piece.getPosition();

            for (Position to : possibleMoves) {
                if (validator.isMoveKingSafe(piece, from, to)) {
                    moves.add(new Move(from, to, piece));
                }
            }
        }
        return moves;
    }

    public static class Move {
        public Position from;
        public Position to;
        public Piece piece;

        public Move(Position from, Position to, Piece piece) {
            this.from = from;
            this.to = to;
            this.piece = piece;
        }

        @Override
        public String toString() {
            return piece.getClass().getSimpleName() + " " + from + " -> " + to;
        }
    }
}
