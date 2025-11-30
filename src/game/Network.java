package game;

import player.Server;
import piece.Piece;
import player.Client;
import utils.Color;
import utils.NetworkMessageType;
import utils.NetworkMessage;
import utils.Position;
import board.Board;
import java.io.*;

public class Network extends GUI {
    private int port;
    private Server server = null;
    private Client client = null;
    private boolean isHost;
    private Color myColor;
    
    // Rollback support for client side optimistic updates 
    private Board backupBoard = null;
    private Color backupTurn = null;
    
    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================
    
    // Constructor for HOST mode
    public Network(boolean isPvP, Color p1Color, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = true;
        this.myColor = p1Color;
        
        server = new Server(p1Color, port);
        server.acceptClient(); // Block until client connects
        
        // Send initial game state to client for sync
        Color clientColor = (p1Color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        server.sendInitialSync(this, clientColor);
    }
    
    // Constructor for CLIENT mode
    public Network(boolean isPvP, Color p1Color, String serverIP, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = false;
        
        client = new Client(p1Color, serverIP, port);
        
        // Receive initial sync from server
        NetworkMessage syncMsg = client.receiveInitialSync();
        if (syncMsg != null) {
            // Update client's color based on server assignment
            this.myColor = syncMsg.clientColor;
            // Sync game state
            if (syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
            }
        }
    }

    // ============================================================================
    // SHARED METHODS (Both host and client use)
    // ============================================================================

    @Override
    public void end(Color winner) {
        // Once a winner is determined, call methods to display who won in a pop up on each playes screen 
    }

    /**
     * Control flow (whose turn, wait for input)
     * 
     */
    @Override
    public void turn() {
        if (WhosTurn == myColor) {
            // LOCAL TURN: Wait for player to click/drag in GUI
            // BoardPanel will call executeTurn() when move is made
        } else {
            // REMOTE TURN: Listen for opponent's move from network
            if (isHost) {
                // Server: receive move request from client, validate, and apply
                //waitForClientMove();
            } else {
                // Client: receive validated move from server and apply
                //waitForServerMove();
            }
        }
    }

    /** 
     * Action execution (validate and apply a specific move)
     * Uses optimistic updates for client moves - immediately applies move to GUI,
     * then validates with server. Rollback occurs if server rejects.
     */
    @Override
    public boolean executeTurn(Position from, Position to, Piece piece) {
        if (WhosTurn == myColor) {
            if (isHost) {
                // Servers Local Move: validate and apply immediately
                boolean success = super.executeTurn(from, to, piece);
                
                if (success) {
                    // Send validated move to client using MOVE_UPDATE
                    server.sendMoveUpdate(from, to);
                }
                
                return success;
            } else {
                // Client local move: Backup game state and perform optimistic update
                backupBoard = deepCopyBoard(board);
                backupTurn = WhosTurn;
                
                boolean success = super.executeTurn(from, to, piece);
                
                if (success) {
                    // Send move to server for authoritative validation
                    client.sendMoveRequest(from, to);
                    // Server will respond async via handleMoveApproval() or handleMoveRejection()
                } else {
                    // Client-side validation failed, clear backup
                    backupBoard = null;
                    backupTurn = null;
                }
                return success;
            }
        } else {
            // REMOTE MOVE (opponent's turn)
            return executeRemoteMove(from, to, piece);
        }
    }

    /**
     * Executes a move received from the network opponent.
     * Applies the validated move without re-validating, then checks for game end conditions.
     * 
     * @param from Starting position of the piece
     * @param to Target position for the move
     * @param piece The piece being moved
     * @return true if move was successfully applied
     */
    private boolean executeRemoteMove(Position from, Position to, Piece piece) {
        // Apply the validated move received from network without re-validating
        Piece capturedPiece = board.getPieceAt(to);
        boolean kingCaptured = false;

        // Handle captures
        if (capturedPiece != null && capturedPiece.getColor() != piece.getColor()) {
            kingCaptured = board.capturePiece(piece, to);
        }

        // Update piece position
        board.updatePiecePosition(piece, from, to);

        // Check if game ended by King capture
        if (kingCaptured) {
            winner = WhosTurn;
            end(winner);
            return true;
        }

        // Check for checkmate using the CheckmateDetector
        player.Player opponent = getOpponentPlayer();
        if (validMoveDetector != null && validMoveDetector.isCheckmate(opponent.getColor())) {
            winner = WhosTurn;
            System.out.println("Checkmate! " + winner + " wins!");
            end(winner);
            return true;
        }

        // Switch turns
        switchTurn();
        
        return true;
    }


    // Syncs game state between host and client (for error recovery)
    public void updateGameState() {
        if (isHost) {
            // Host sends current game state to client
            Color clientColor = (myColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
            server.sendInitialSync(this, clientColor);
        } else {
            // Client receives updated game state from host
            NetworkMessage syncMsg = client.receiveInitialSync();
            if (syncMsg != null && syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
            }
        }
    }
    
    public Color getMyColor() {
        return myColor;
    }
    
    /**
     * Deep copy of Board using serialization.
     * Leverages existing Serializable implementation.
     * 
     * @param original The board to copy
     * @return A deep copy of the board
     */
    private Board deepCopyBoard(Board original) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();
            
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (Board) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to deep copy board: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Called when server approves the client's optimistic move.
     * Clears the backup state as the move is confirmed valid.
     */
    public void handleMoveApproval() {
        // Move was valid, just clear backup
        backupBoard = null;
        backupTurn = null;
    }
    
    /**
     * Called when server rejects the client's optimistic move.
     * Rolls back the board state to the pre-move backup.
     * 
     * @param errorMessage Optional error message to display to user
     */
    public void handleMoveRejection(String errorMessage) {
        // Restore backup state
        this.board = backupBoard;
        this.WhosTurn = backupTurn;
        
        // Update GUI to reflect rollback
        refreshBoardPanel();
        
        // Show error to user
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showInvalidMoveMessage();
        }
        
        // Clear backup
        backupBoard = null;
        backupTurn = null;
    }

    // ============================================================================
    // HOST-ONLY METHODS (Server-specific functionality)
    // ============================================================================

    // Handle incoming move request from client (only for host)
    public void handleMoveRequest() {
        if (!isHost) {
            throw new IllegalStateException("Only server can handle move requests");
        }

        NetworkMessage request = server.receiveMoveRequest();
        if (request != null) {
            // Validate Move needs to use CheckMateDetector
            
            // Send validation response back to client
            
        }
    }



    // ============================================================================
    // CLIENT-ONLY METHODS (Client-specific functionality)
    // ============================================================================


}
