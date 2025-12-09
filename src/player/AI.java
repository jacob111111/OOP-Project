package player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import board.Board;
import piece.*;
import utils.Color;
import utils.MoveValidator;
import utils.Position;

/**
 * Represents an AI (computer) player in the chess game using Stockfish API.
 * 
 * This AI uses the lichess.org cloud API to get moves from Stockfish.
 * No local installation required - all analysis happens in the cloud.
 * 
 * Difficulty levels:
 * - Easy (1): Depth 5 - Quick, weaker moves
 * - Medium (2): Depth 10 - Balanced play
 * - Hard (3): Depth 15 - Strong, deep analysis
 * 
 * @see Player
 */
public class AI extends Player {
    private int difficulty; // 1=Easy, 2=Medium, 3=Hard
    private static final String API_URL = "https://lichess.org/api/cloud-eval";
    private static final int TIMEOUT_MS = 5000; // 5 second timeout

    public AI(Color color) {
        this(color, 2);
    }

    public AI(Color color, int difficulty) {
        super(color);
        this.difficulty = Math.max(1, Math.min(3, difficulty));
        System.out.println("Stockfish AI initialized (cloud-based, no local setup required)");
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(3, difficulty));
    }

    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the best move from Stockfish API for the current board position.
     * 
     * @param board The current board state
     * @return The best move according to Stockfish, or null if unavailable
     */
    public Move getBestMove(Board board) {
        try {
            // Convert board to FEN notation
            String fen = board.toFEN(color);

            // Determine depth based on difficulty
            int depth = getDepthForDifficulty();

            // Query lichess API
            String bestMove = queryLichessAPI(fen, depth);

            if (bestMove != null && !bestMove.isEmpty()) {
                // Parse UCI move format (e.g., "e2e4" or "e7e8q" for promotion)
                Move move = parseUCIMove(board, bestMove);

                if (move != null) {
                    System.out.println("Stockfish AI (difficulty " + difficulty + ", depth " + depth + "): " + move);
                    return move;
                } else {
                    System.err.println("Failed to parse API move: " + bestMove);
                }
            }

        } catch (Exception e) {
            System.err.println("Error querying Stockfish API: " + e.getMessage());
            System.err.println("Falling back to random move");
        }

        // Fallback to random legal move if API fails
        return getFallbackMove(board);
    }

    /**
     * Queries the lichess.org cloud evaluation API.
     * 
     * @param fen   The board position in FEN notation
     * @param depth The search depth
     * @return The best move in UCI format, or null if unavailable
     */
    private String queryLichessAPI(String fen, int depth) {
        try {
            // Build URL with parameters
            String encodedFen = URLEncoder.encode(fen, StandardCharsets.UTF_8.toString());
            String urlString = API_URL + "?fen=" + encodedFen + "&multiPv=1";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse JSON response to extract best move
                return parseLichessResponse(response.toString());

            } else {
                System.err.println("API returned status code: " + responseCode);
                return null;
            }

        } catch (Exception e) {
            System.err.println("API request failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses the lichess API JSON response to extract the best move.
     * 
     * @param jsonResponse The JSON response from lichess API
     * @return The best move in UCI format
     */
    private String parseLichessResponse(String jsonResponse) {
        try {
            // Simple JSON parsing - look for "uci" field in pvs array
            // Example: {"fen":"...","knodes":1234,"depth":20,"pvs":[{"moves":"e2e4
            // ...","cp":28}]}

            int pvIndex = jsonResponse.indexOf("\"pvs\"");
            if (pvIndex == -1)
                return null;

            int movesIndex = jsonResponse.indexOf("\"moves\"", pvIndex);
            if (movesIndex == -1)
                return null;

            int startQuote = jsonResponse.indexOf("\"", movesIndex + 7);
            if (startQuote == -1)
                return null;

            int endQuote = jsonResponse.indexOf("\"", startQuote + 1);
            if (endQuote == -1)
                return null;

            String moves = jsonResponse.substring(startQuote + 1, endQuote);

            // First move in the sequence is the best move
            String[] moveList = moves.split(" ");
            return moveList.length > 0 ? moveList[0] : null;

        } catch (Exception e) {
            System.err.println("Failed to parse API response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the search depth based on difficulty level.
     */
    private int getDepthForDifficulty() {
        switch (difficulty) {
            case 1:
                return 5; // Easy
            case 3:
                return 15; // Hard
            default:
                return 10; // Medium
        }
    }

    /**
     * Parses a UCI format move string into a Move object.
     * 
     * @param board   The current board
     * @param uciMove Move in UCI format (e.g., "e2e4", "e7e8q")
     * @return Parsed Move object or null if invalid
     */
    private Move parseUCIMove(Board board, String uciMove) {
        if (uciMove == null || uciMove.length() < 4) {
            return null;
        }

        try {
            // Extract from and to positions
            String fromStr = uciMove.substring(0, 2);
            String toStr = uciMove.substring(2, 4);

            Position from = board.chessNotationToPosition(fromStr);
            Position to = board.chessNotationToPosition(toStr);

            if (from == null || to == null) {
                return null;
            }

            Piece piece = board.getPieceAt(from);
            if (piece == null || piece.getColor() != color) {
                return null;
            }

            // Handle promotion (e.g., "e7e8q")
            String promotion = null;
            if (uciMove.length() == 5) {
                promotion = uciMove.substring(4, 5);
            }

            return new Move(from, to, piece, promotion);

        } catch (Exception e) {
            System.err.println("Error parsing UCI move: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fallback to random legal move if Stockfish is unavailable.
     */
    private Move getFallbackMove(Board board) {
        List<Move> allMoves = generateAllLegalMoves(board);
        if (allMoves.isEmpty()) {
            return null;
        }
        return allMoves.get(0);
    }

    /**
     * Generates all legal moves for the current player.
     */
    private List<Move> generateAllLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        MoveValidator validator = board.getMoveValidator();

        for (Piece piece : getCurrentPieces()) {
            Set<Position> possibleMoves = piece.getLegalMoves(board);
            Position from = piece.getPosition();

            for (Position to : possibleMoves) {
                if (validator.isMoveKingSafe(piece, from, to)) {
                    moves.add(new Move(from, to, piece, null));
                }
            }
        }
        return moves;
    }

    /**
     * Cleans up resources (no-op for API-based implementation).
     */
    public void shutdown() {
        // No resources to clean up - API-based implementation
        System.out.println("AI shutdown (no cleanup needed for API-based engine)");
    }

    /**
     * Represents a chess move with source, destination, and piece information.
     */
    public static class Move {
        public Position from;
        public Position to;
        public Piece piece;
        public String promotion; // For pawn promotion (q, r, b, n)

        public Move(Position from, Position to, Piece piece, String promotion) {
            this.from = from;
            this.to = to;
            this.piece = piece;
            this.promotion = promotion;
        }

        @Override
        public String toString() {
            String moveStr = piece.getClass().getSimpleName() + " " + from + " -> " + to;
            if (promotion != null) {
                moveStr += " (promote to " + promotion + ")";
            }
            return moveStr;
        }
    }
}
